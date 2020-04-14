package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements;

import edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.IEventSimHierarchyService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

class CodeElementMapper {

    // elements unwanted in names
    private static String[] unwantedElements = {"id","this"};

    /**
     * maps the loaded or stored string referenced local variable number to an integer
     *
     * @param methodCode string representation of the corresponding local variable number
     * @return the corresponding local variable number
     */
    static int getVarNr(String[] methodCode){
        int localVarNr = -1;
        if(methodCode.length == 2){
            if(methodCode[1].contains("0")){
                localVarNr=0;
            } else if(methodCode[1].contains("1")){
                localVarNr=1;
            } else if(methodCode[1].contains("2")){
                localVarNr=2;
            } else if(methodCode[1].contains("3")){
                localVarNr=3;
            }
        } else{
            try{
                localVarNr = Integer.parseInt(methodCode[2].substring(1));
            }catch(Exception ignored){

            }
        }
        return localVarNr;
    }

    /**
     * checks if a considered name class name matches the name of an super class or interface name
     *
     * @param oldName name of the considered child class
     * @param potentialName name of the class that is requested
     * @param eventSimHierarchyService service class, that delivers the class hierarchy of the sy
     * @return either the starting name or the name of an super class that matches with the requested one
     */
    static String getAbstractSuperName(String oldName, String potentialName, IEventSimHierarchyService eventSimHierarchyService) {
        //receive classHierarchie of a class
        Collection<String> allDerivedClassNames = eventSimHierarchyService.getDerivedClasses(oldName);
        //check if methode is derivced from class
        if(allDerivedClassNames.size() >1){
            for (String newName: allDerivedClassNames) {
                if(newName.endsWith("." + potentialName)){
                    return newName;
                }
            }
        }
        return oldName;
    }

    /**
     * Finds the nam of the local variable specified by the local variable number
     *
     * @param localVariables list of local variables
     * @param varNumber number of the considered local variable
     * @return the name of the considered local variable
     */
    static String getLocalVar(String[][] localVariables, String varNumber){
        for (String[] localVar:localVariables) {
            if(localVar[0].equals(varNumber)){
                return localVar[1] + "_" + localVar[2];
            }
        }
        return "empty";
    }

    /**
     * reads the type of an variable from the list of local variables
     *
     * @param localVariables list of local variables
     * @param varName name of the considered local variable
     * @return the type of the local variable or "unknown"
     */
    static String getVarType(String[][] localVariables, String varName){
        String type = "unknown";
        for (String[] singleLocalVariable : localVariables){
            if(singleLocalVariable[2].equals(varName)){
                type = singleLocalVariable[1].split(Pattern.quote("."))[singleLocalVariable[1].split(Pattern.quote(".")).length-1];
                break;
            }
        }
        return type;
    }

    /**
     * Prepares the String representing the name of invoked methods
     *
     * @param invokeStr the original name of the invoked method
     * @return array with [0] containing the class name and [1] containing the method name
     */
    static String[] getInvokeMethodName(String invokeStr){
        String[] splitName = invokeStr.split(Pattern.quote("."));
        String[] returnArray = new String[2];
        returnArray[0] = invokeStr.substring(0, invokeStr.length()-splitName[splitName.length-1].length() -1);
        returnArray[1] = splitName[splitName.length-1];
        return returnArray;
    }

    /**
     * checks the previous to a line executed commands in order to figure out, which variables are passed as parameters
     *
     * @param code the byte code of the considered method
     * @param line the considered line of the byte code
     * @return array of local variables that are loaded previous to the specified code line
     */
    static String[][] getPreviousLoadLine(String[][] code, int line){
        String[] loadLocalVarByteCode = {"get"};
        String[] loadOtherElementsByteCode = {"load","ldc"};
        ArrayList<String[]> returnArray = new ArrayList<>();
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
                    break;
                }
            }
            if(!found){
                break;
            }
        }
        String[][] codeLines = new String[returnArray.size()][1];
        int i=0;
        for (String[] codeLine: returnArray) {
            codeLines[i++] = codeLine;
        }
        return codeLines;
    }

    /**
     * checks if the variable that is referred to, has been called referred by a store command
     *
     * @param code the byte code of the corresponding method
     * @param line the line, previous to which the store command should be checked
     * @param varNumber the local variable number of the considered variable
     * @return false, iff the the locale variable is stored
     */
    static boolean checkIfValueIsNotPrevStored(String[][] code, int line, int varNumber) {
        for (int i = 0; i <=line; i++) {
            if (code[i][1].contains("store")) {
                if(getVarNr(code[i]) == varNumber){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks whether the value on the stack is stored or a new value is loaded
     *
     * @param code the corresponding code of the considered method
     * @param line the line from which on the value is considered to be load or stored
     * @return iff the value if stored in an local variable
     */
    static boolean checkIfValueIsPut(String[][] code, int line){
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

    /**
     * checks if a string contains specified substrings
     *
     * @param varString the string to check for containing elements
     * @return iff one of the string if contained
     */
    static boolean matchingVar(String varString){
        for (String string: unwantedElements) {
            if(varString.toLowerCase().contains(string.toLowerCase())){
                return false;
            }
        }
        return true;
    }
}
