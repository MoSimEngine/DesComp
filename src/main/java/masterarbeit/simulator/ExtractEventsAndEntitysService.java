package masterarbeit.simulator;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class ExtractEventsAndEntitysService {

	public static void main(String[] args) {

		try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("com.xyz").scan()) {
			ClassInfoList checkedBoxes = scanResult.getSubclasses("com.xyz.Box");

			for (ClassInfo classInfo : checkedBoxes) {
				System.out.println(classInfo.getName());
			}

		}
	}

}
