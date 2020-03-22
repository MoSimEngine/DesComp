package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.dataElementCreator;

import org.apache.bcel.classfile.*;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MethodeDecoder {


    //<MethodeName, <RelationType, <AffectedObjName, Collection<AttrNames>>>>
    public static HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractEventsFromMethods(HashMap<String, Method> methodCollection){
        HashMap<String, String[][][]> methodeDecoded = new HashMap<>();
        for (String str:methodCollection.keySet()) {
            Code code = methodCollection.get(str).getCode();
            String[][][] methodeExtraction = splitCodeSegments(code.toString());
            methodeDecoded.put(str, methodeExtraction);
        }

        HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> result = new HashMap<>();
        for (String str: methodeDecoded.keySet()) {
            result.put(str, getConcreteRelationsEvents(methodeDecoded.get(str), str));
        }
        return result;
    }


    //arr[0] -> code zeilen
    //  arr[0][+] -> einzelne codezeilen
    //arr[1] -> locale variabeln
    //  arr[1][0] -> local var number
    //  arr[1][1] -> var type
    //  arr[1][2] -> var name
    private static String[][][] splitCodeSegments(String codeStream){
        String[] lineCode = codeStream.split("\n");

        String[][][] sortedCode = new String[2][lineCode.length][4];

        int foundCodeLine =0;
        int foundLocalVariable =0;
        for(int line =0; line < lineCode.length; line++) {

            if(lineCode[line].matches("[0-9]+:.*$")){
                sortedCode[0][foundCodeLine++] = Arrays.asList(lineCode[line].split("[\\t& ]")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
            }else if(lineCode[line].matches("LocalVariable.*$")){
                sortedCode[1][foundLocalVariable++] = Arrays.asList(lineCode[line].split("[:&)]")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
            }
        }
        sortedCode[0] = extractCodeLines(sortedCode[0], foundCodeLine);
        sortedCode[1] = extractLocalVariable(sortedCode[1], foundLocalVariable);


        return sortedCode;
    }

    private static String[][] extractCodeLines(String[][] extractedLines, int lineAmount){
        String[][] arrayhelp = new String[lineAmount][1];
        System.arraycopy(extractedLines, 0, arrayhelp, 0, lineAmount);
        return arrayhelp;
    }

    private static String[][] extractLocalVariable(String[][] extractedStuff, int localVarAmount){
        String[][] resultArray = new String[localVarAmount][3];
        int elementLength =0;
        for(int i=0; i<localVarAmount; i++){
            if(!ArrayUtils.isEmpty(extractedStuff[i])){
                elementLength++;
                String[] array = Arrays.asList(extractedStuff[i][0].split("index = ")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
                String[] array2 = Arrays.asList(extractedStuff[i][1].split(" ")).stream().filter(str -> !str.isEmpty()).collect(Collectors.toList()).toArray(new String[0]);
                resultArray[i][0] = array[1];
                resultArray[i][1] = array2[0];
                resultArray[i][2] = array2[1];
            }
        }
        return resultArray;
    }

    //dreturn -> writes in vorherigen dload(sofern aufrufquelle nicht vorhanden)
    //athrow -> wirft fehler; vorher entscheiden das alles was vorher bis zum # passiert, teil der fehlerbehandlung
    //*store vor *load -> neu erstellt
    // reutrn value -> gucken
    //Read/write/schedule(Object(attribute))
    private static HashMap<String, HashMap<String, Collection<String>>> getConcreteRelationsEvents(String[][][] methodeCode, String key){
        HashMap<String, HashMap<String, Collection<String>>> effectSpecification = new HashMap<>();
        effectSpecification.put("read", new HashMap<>());
        effectSpecification.put("write", new HashMap<>());
        effectSpecification.put("schedule", new HashMap<>());

        //looking at java byte code and evaluating them
        for(int i =0 ; i< methodeCode[0].length; i++){
            if(methodeCode[0][i][1].startsWith("get")){
                String[] effectiveCode = methodeCode[0][i][2].split(Pattern.quote("."));
                HashMap<String,Collection<String>> readMap = effectSpecification.get("read");
                if(!readMap.keySet().contains(effectiveCode[effectiveCode.length-2])){
                    Collection<String> attrCollection = new HashSet<>();
                    readMap.put(effectiveCode[effectiveCode.length-2], attrCollection);
                }
                readMap.get(effectiveCode[effectiveCode.length-2]).add(effectiveCode[effectiveCode.length-1]);
            }
            else if(methodeCode[0][i][1].startsWith("put")){
                HashMap<String, Collection<String>> writeMap = effectSpecification.get("write");
                String[] effectiveCode = methodeCode[0][i][2].split(Pattern.quote("."));
                if(!writeMap.keySet().contains(effectiveCode[effectiveCode.length-2])){
                    Collection<String> attrCollection = new HashSet<>();
                    writeMap.put(effectiveCode[effectiveCode.length-2], attrCollection);
                }
                writeMap.get(effectiveCode[effectiveCode.length-2]).add(effectiveCode[effectiveCode.length-1]);
            }
            else if(methodeCode[0][i][1].contains("load")){
                int localVar = getVarNr(methodeCode[0][i]);
                if(localVar>=0 && checkIfValuePrevStored(methodeCode[0], i, methodeCode[1], localVar) && matchingVar(getLocalVar(methodeCode[1] , Integer.toString(localVar)))) {
                    HashMap<String,Collection<String>> readMap = effectSpecification.get("read");
                    if(!readMap.containsKey("caller")){
                        Collection<String> attrCollection = new HashSet<>();
                        readMap.put("caller", attrCollection);
                    }
                    readMap.get("caller").add(key + "_" + getLocalVar(methodeCode[1] , Integer.toString(localVar)));
                }

            }

            //caller => das was die methode aufruft, hat die reff das dort hinein geschrieben wird (ggf.)
            else if(methodeCode[0][i][1].contains("return")&& !methodeCode[0][i][1].equals("return")){
                HashMap<String,Collection<String>> writeMap  = effectSpecification.get("write");
                if(!writeMap.keySet().contains("caller")){
                    Collection<String> attrCollection = new HashSet<>();
                    writeMap.put("caller", attrCollection);
                }
                writeMap.get("caller").add(key);
            }

            else if(methodeCode[0][i][1].contains("invoke")){
                //store -> wenn vorher ein methodenaufruf -> dann called die externe methode
                // invoke -> danach ein store, ohne load vorher => write????
                // write called, read called?
                HashMap<String, Collection<String>> schedulingMap = effectSpecification.get("schedule");
                String[] callingName = getinvokeMethodeName(methodeCode[0][i][2]);
                //filters for only relevant edu evets
                if((methodeCode[0][i][2].contains("de") || methodeCode[0][i][2].contains("edu") || methodeCode[0][i][2].contains("uka"))){
                    if(!schedulingMap.containsKey(callingName[0])){
                        Collection<String> attrCollection = new HashSet<>();
                        schedulingMap.put(callingName[0], attrCollection);
                    }
                    schedulingMap.get(callingName[0]).add(callingName[1]);


                    String calledMethodClassName = callingName[0].split(Pattern.quote("."))[callingName[0].split(Pattern.quote(".")).length - 1];
                    String calledMethodName = callingName[1];

                    //invoke. die danach aufgerufene put methode
                    if(checkIfValueIsPut(methodeCode[0], i)){
                        Collection<Integer> storeMethodePlaces = new ArrayList<>();
                        for (int j = i; j < methodeCode[0].length; j++) {
                            if(methodeCode[0][j][1].contains("put") || methodeCode[0][j][1].contains("store")){
                                storeMethodePlaces.add(j);
                            } else if(methodeCode[0][j][1].contains("get") || methodeCode[0][j][1].contains("load")){
                                break;
                            }
                        }

                        for (int locationInt:storeMethodePlaces) {
                            String writesTo = getLocalVar(methodeCode[1], Integer.toString(getVarNr(methodeCode[0][locationInt])));
                            HashMap<String, Collection<String>> writeMap = effectSpecification.get("write");
                            if (!writeMap.containsKey("called_" + callingName[1])) {
                                Collection<String> attrCollection = new HashSet<>();
                                writeMap.put("called_" + calledMethodClassName+ "_"+calledMethodName, attrCollection);
                            }
                            writeMap.get("called_" + calledMethodClassName+ "_"+calledMethodName).add(writesTo);
                        }
                    }

                    //TODO die davor aufgerufenden load als read referenz für called -> called_ in read
                    for (String[] prevLoads:getPreviousLoadLine(methodeCode[0],i)) {
                        if (prevLoads[1].startsWith("get"))
                        {
                            String[] loadedVariableName = prevLoads[2].split(Pattern.quote("."));
                            HashMap<String, Collection<String>> readMap = effectSpecification.get("read");

                            if (!readMap.keySet().contains("called_" + calledMethodClassName)) {
                                Collection<String> attrCollection = new HashSet<>();
                                readMap.put("called_" + calledMethodClassName+ "_"+calledMethodName, attrCollection);
                            }
                            readMap.get("called_" + calledMethodClassName+ "_"+calledMethodName).add(loadedVariableName[loadedVariableName.length-1]);
                        }
                    }

                }
            }
        }
        return effectSpecification;
    }

    private static int getVarNr(String[] methodeCode){
        int localVarNr = -1;
        if(methodeCode.length == 2){
            if(methodeCode[1].contains("0")){
                localVarNr=0;
            } else if(methodeCode[1].contains("1")){
                localVarNr=1;
            } else if(methodeCode[1].contains("2")){
                localVarNr=2;
            } else if(methodeCode[1].contains("3")){
                localVarNr=3;
            }
        } else{
            localVarNr = Integer.parseInt(methodeCode[2].substring(1));
        }
        return localVarNr;
    }

    private static String getLocalVar(String[][] localVariables, String varNumber){
        for (String[] localVar:localVariables) {
            if(localVar[0].equals(varNumber)){
                return localVar[1] + "_" + localVar[2];
            }
        }
        return "empty";
    }

    private static boolean checkIfValuePrevStored(String[][] code, int line, String[][] localVar, int varNumber) {
        for (int i = 0; i <=line; i++) {
            if (code[i][1].contains("store")) {
                if(getVarNr(code[i]) == varNumber){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkIfValueIsPut(String[][] code, int line){
        for (int i = line; i < code.length; i++) {
            if(code[i].length >= 2) {
                if (code[i][1].contains("load") || code[i][1].contains("get")) {
                    return false;
                } else if (code[i][1].contains("put") || code[i][1].contains("store")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchingVar(String varString){
        String[] unwantedElements = {"id","this"};
        for (String string: unwantedElements) {
            if(varString.toLowerCase().contains(string.toLowerCase())){
                return false;
            }
        }
        return true;

    }

    private static String[] getinvokeMethodeName(String invokeStr){
        String[] splitName = invokeStr.split(Pattern.quote("."));
        String[] returnArray = new String[2];
        returnArray[0] = invokeStr.substring(0, invokeStr.length()-splitName[splitName.length-1].length() -1);
        returnArray[1] = splitName[splitName.length-1];
        return returnArray;
    }

    private static String[][] getPreviousLoadLine(String[][] code, int line){
        String[] loadLocalVarByteCode = {"get"};
        String[] loadOtherElementsByteCode = {"load","ldc"};
        ArrayList<String[]> returnArray = new ArrayList<String[]>();
        for (int i = line-1 ; i >=0 ; i--) {
            boolean found = false;
            for (String str:loadLocalVarByteCode) {
                if (code[i][1].startsWith(str)) {
                    returnArray.add(code[i]);
                    found = true;
                }
            }
            for (String str: loadOtherElementsByteCode) {
                if(code[i][1].startsWith(str)){
                    found = true;
                }
            }
            if(!found){
                break;
            }
        }
        String[][] codeLines = new String[returnArray.size()][1];
        int i=0;
        for (String[] codeLine: returnArray) {
            codeLines[i] = codeLine;
        }

        return codeLines;
    }

}
