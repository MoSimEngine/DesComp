package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.IEventSimHierarchyService;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import static edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.CodeElementMapper.*;
import static edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements.CodeSegmentSplittingService.splitCodeSegments;

public class MethodDecoder {

    public static String write = "write";
    public static String read = "read";
    public static String schedule = "schedule";
    private static IEventSimHierarchyService eventSimExtractorService;

    /**
     * Decodes the information for in the structural graph to model objects
     *  1)preparing the methodCollection, extracting String representation of those methods
     *  2)filtering those structural information from the prepared String representation
     *
     * @param methodCollection hashMap with considered methods, key is method name, value bcel.Method Object
     * @param service class that provides functionality to work on original class hierarchy
     * @return decoded Methods; Format: <MethodName, <RelationType(Read/Write/Schedule), <AffectedObjName(EntityName/EventName), Collection<AttrNames(Event.Attribute/scheduledEvent)>>>>
     */
    public static HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> extractEventsFromMethods(HashMap<String, Method> methodCollection, IEventSimHierarchyService service){
        eventSimExtractorService = service;

        //1)preparing the methodCollection, extracting String representation of those methods
        HashMap<String, String[][][]> methodsExtraction = new HashMap<>();
        for (String str:methodCollection.keySet()) {
            Code code = methodCollection.get(str).getCode();
            String[][][] methodExtraction = splitCodeSegments(code.toString());
            methodsExtraction.put(str, methodExtraction);
        }

        //2)filtering those structural information from the prepared String representation
        HashMap<String, HashMap<String, HashMap<String, Collection<String>>>> result = new HashMap<>();
        for (String str: methodsExtraction.keySet()) {
            result.put(str, getConcreteRelationsEvents(methodsExtraction.get(str), str));
        }
        return result;
    }

    /**
     * Filters structural information from prepared String representation
     *  Considered byteCode elements: get, load, put, return, invoke
     *      -get
     *          adds read relation of the specified attribute to the structural information
     *      -load
     *          adds read relation of the specified attribute from the calling entity to the structural information, iff that attribute is passed
     *      -put
     *          adds write relation to the specified attribute to the structural information
     *      -return
     *          adds write relation to the specified attribute from the calling entity to the structural information, iff an value is returned
     *      -invoke
     *          filters for as relevant considered scheduled events
     *          adding scheduling relation for the invoked method
     *          add structural write information if called method return value affect entity attributes
     *          add structural read information for the invoked method iff local variables are passed as invoke parameters
     *
     * @param methodeCode wrapped byte code representation of the considered method
     * @param key name of the considered method
     * @return decoded Method; Format: <RelationType(Read/Write/Schedule), <AffectedObjName(EntityName/EventName), Collection<AttrNames(Event.Attribute/scheduledEvent)>>>
     */
    private static HashMap<String, HashMap<String, Collection<String>>> getConcreteRelationsEvents(String[][][] methodeCode, String key){
        HashMap<String, HashMap<String, Collection<String>>> effectSpecification = new HashMap<>();
        effectSpecification.put("read", new HashMap<>());
        effectSpecification.put("write", new HashMap<>());
        effectSpecification.put("schedule", new HashMap<>());

        //looking at java byte code and evaluating them
        for(int i =0 ; i< methodeCode[0].length; i++){
            if(methodeCode[0][i][1].startsWith("get")){
                //adds read relation of the specified attribute to the structural information
                String[] effectiveCode = methodeCode[0][i][2].split(Pattern.quote("."));
                HashMap<String,Collection<String>> readMap = effectSpecification.get("read");
                if(!readMap.keySet().contains(effectiveCode[effectiveCode.length-2])){
                    Collection<String> attrCollection = new HashSet<>();
                    readMap.put(effectiveCode[effectiveCode.length-2], attrCollection);
                }
                readMap.get(effectiveCode[effectiveCode.length-2]).add(effectiveCode[effectiveCode.length-1]);
            }

            else if(methodeCode[0][i][1].contains("load")){
            //adds read relation of the specified attribute from the calling entity to the structural information, iff that attribute is passed
                int localVar = getVarNr(methodeCode[0][i]);
                if(localVar>=0 && checkIfValueIsNotPrevStored(methodeCode[0], i, localVar) && matchingVar(getLocalVar(methodeCode[1] , Integer.toString(localVar)))) {
                    HashMap<String,Collection<String>> readMap = effectSpecification.get("read");
                    if(!readMap.containsKey("caller")){
                        Collection<String> attrCollection = new HashSet<>();
                        readMap.put("caller", attrCollection);
                    }
                    readMap.get("caller").add(key + "_" + getLocalVar(methodeCode[1] , Integer.toString(localVar)));
                }
            }

            else if(methodeCode[0][i][1].startsWith("put")){
            //adds write relation to the specified attribute to the structural information
                HashMap<String, Collection<String>> writeMap = effectSpecification.get("write");
                String[] effectiveCode = methodeCode[0][i][2].split(Pattern.quote("."));
                String attributeType =  getVarType(methodeCode[1], effectiveCode[effectiveCode.length-1]);
                if (!attributeType.equals("unknown")) {
                    if(!writeMap.keySet().contains(effectiveCode[effectiveCode.length-2])){
                        Collection<String> attrCollection = new HashSet<>();
                        writeMap.put(effectiveCode[effectiveCode.length-2], attrCollection);
                    }
                    writeMap.get(effectiveCode[effectiveCode.length-2]).add(attributeType + "_" + effectiveCode[effectiveCode.length-1]);
                }
            }

            else if(methodeCode[0][i][1].contains("return")&& !methodeCode[0][i][1].equals("return")){
            //adds write relation to the specified attribute from the calling entity to the structural information, iff an value is returned
                HashMap<String,Collection<String>> writeMap  = effectSpecification.get("write");
                if(!writeMap.keySet().contains("caller")){
                    Collection<String> attrCollection = new HashSet<>();
                    writeMap.put("caller", attrCollection);
                }
                writeMap.get("caller").add(key);
            }

            else if(methodeCode[0][i][1].contains("invoke")){
            //adds scheduling relations to the structural information
                HashMap<String, Collection<String>> schedulingMap = effectSpecification.get("schedule");
                String[] callingName = getInvokeMethodName(methodeCode[0][i][2]);

                //filters for only relevant edu events; here name spaces that starts with "de" or "edu". in order to ignore e.g. java util calls
                if((methodeCode[0][i][2].contains("de") || methodeCode[0][i][2].contains("edu") || methodeCode[0][i][2].contains("uka"))){
                    String realCallingName = getAbstractSuperName(callingName[0], key.split("_")[0], eventSimExtractorService);

                    //adding scheduling relation for the invoked method
                    if(!schedulingMap.containsKey(realCallingName)){
                        Collection<String> attrCollection = new HashSet<>();
                        schedulingMap.put(realCallingName, attrCollection);
                    }
                    schedulingMap.get(realCallingName).add(callingName[1]);

                    String calledMethodClassName = callingName[0].split(Pattern.quote("."))[callingName[0].split(Pattern.quote(".")).length - 1];
                    String calledMethodName = callingName[1];

                    //add structural write information if called method return value affect entity attributes
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
                            if(!writesTo.equals("empty")){
                                HashMap<String, Collection<String>> writeMap = effectSpecification.get("write");
                                if (!writeMap.containsKey("called_" + callingName[1])) {
                                    Collection<String> attrCollection = new HashSet<>();
                                    writeMap.put("called_" + callingName[0] + "_"+calledMethodName, attrCollection);
                                }
                                writeMap.get("called_" + callingName[0]+ "_"+calledMethodName).add(writesTo);
                            }
                        }
                    }

                    //add structural read information for the invoked method iff local variables are passed as invoke parameters
                    for (String[] prevLoads:getPreviousLoadLine(methodeCode[0],i)) {
                        if (prevLoads[1].startsWith("get"))
                        {
                            String[] loadedVariableName = prevLoads[2].split(Pattern.quote("."));
                            HashMap<String, Collection<String>> readMap = effectSpecification.get("read");

                            if (!readMap.keySet().contains("called_" + calledMethodClassName)) {
                                Collection<String> attrCollection = new HashSet<>();
                                readMap.put("called_" + callingName[0]+ "_"+calledMethodName, attrCollection);
                            }
                            readMap.get("called_" + callingName[0]+ "_"+calledMethodName).add(loadedVariableName[loadedVariableName.length-1]);
                        }
                    }

                }
            }
        }
        return effectSpecification;
    }
}
