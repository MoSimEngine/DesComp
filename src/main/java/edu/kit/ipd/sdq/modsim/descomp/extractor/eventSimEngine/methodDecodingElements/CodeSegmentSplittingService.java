package edu.kit.ipd.sdq.modsim.descomp.extractor.eventSimEngine.methodDecodingElements;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

class CodeSegmentSplittingService {

    /**
     * Transforms byte code stream into a 3 dimensional array
     *
     * @param codeStream original byte code sequence of a method
     * @return  splitted and fromated Code array; Array {Code, localVariables};
     *          with
     *              code:= String[][]; D1 -> representing that code line, D2 -> containing splitted code
     *              localVariables := String[][]; D1 -> local variable number, D2 -> localVariable name
     */
    static String[][][] splitCodeSegments(String codeStream){
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
}
