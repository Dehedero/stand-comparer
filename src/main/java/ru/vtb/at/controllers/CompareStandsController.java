package ru.vtb.at.controllers;


import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.vtb.at.stand_comparer.AlphabeticComparator;
import ru.vtb.at.stand_comparer.CompareData;
import ru.vtb.at.stand_comparer.StandComparer;

import java.util.*;

import static ru.vtb.at.stand_comparer.CompareData.NA;

@Controller
public class CompareStandsController {
    @Autowired
    StandComparer standComparer;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/compareStands")
    public String comparerPage(Model model) {
        return "stand_compare_page";
    }

    @PostMapping("/compareStands/compare")
    public String compareStands(Model model, String standA, String standB, String loadFile, MultipartFile formFile) {
        model.addAttribute("standA", standA);
        model.addAttribute("standB", standB);
        Map<String, String> firstServicesMap;
        Map<String, String> secondServicesMap;

        try {
            firstServicesMap = standComparer.getServicesMap(standComparer.getRawData(standA));

            if (Strings.isEmpty(loadFile))
                secondServicesMap = standComparer.getServicesMap(standComparer.getRawData(standB));
            else
                secondServicesMap = standComparer.getServicesMap(formFile);
        } catch (Exception e) {
            model.addAttribute("resultCommom", e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return "stand_compare_page";
        }

        Map<String, String[]> standsDifferences = standComparer.getStandsDifferences(firstServicesMap, secondServicesMap);

        int serviceColumnWidth = 60;
        String formatHeader = "%-" + serviceColumnWidth + "s%-" + serviceColumnWidth + "s";
        String headers = String.format(formatHeader, standA.replaceAll("\\..+", ""), standB.replaceAll("\\..+", ""));

        List<CompareData> allDataList = new LinkedList<>();
        List<CompareData> compareDataListX = new LinkedList<>();
        List<CompareData> compareDataListBigger = new LinkedList<>();
        List<CompareData> compareDataListLess = new LinkedList<>();

        StringBuilder allDataSB = new StringBuilder();
        StringBuilder majorVersionsSB = new StringBuilder();
        allDataSB.append(headers).append(System.lineSeparator());
        for (Map.Entry<String, String[]> entry : standsDifferences.entrySet()) {
            String service = entry.getKey();
            String firstVersion = entry.getValue()[0] != null ? entry.getValue()[0] : NA;
            String secondVersion = entry.getValue()[1] != null ? entry.getValue()[1] : NA;
            String comparison = standComparer.compareVersions(firstVersion, secondVersion);
            CompareData data = new CompareData(service, firstVersion, secondVersion, comparison);
            allDataSB.append(data.getFormatted(50));
            majorVersionsSB.append(data.getMajor());
            allDataList.add(data);
            if (comparison.equals("x"))
                compareDataListX.add(data);
            else if (comparison.equals(">"))
                compareDataListBigger.add(data);
            else
                compareDataListLess.add(data);
        }
        AlphabeticComparator alphabeticComparator = new AlphabeticComparator();
        compareDataListX.sort(alphabeticComparator);
        compareDataListBigger.sort(alphabeticComparator);
        compareDataListLess.sort(alphabeticComparator);

        StringBuilder aVersionsSBDown = new StringBuilder();
        StringBuilder bVersionsSBUp = new StringBuilder();

        aVersionsSBDown.append("Downgrade:").append(System.lineSeparator());
        bVersionsSBUp.append("Upgrade:").append(System.lineSeparator());
        for (CompareData data : compareDataListBigger) {
            aVersionsSBDown.append(data.getBVersion());
            bVersionsSBUp.append(data.getAVersion());
        }

        bVersionsSBUp.append(System.lineSeparator()).append("------------------------------").append(System.lineSeparator());
        aVersionsSBDown.append(System.lineSeparator()).append("------------------------------").append(System.lineSeparator()).append("Deploy:").append(System.lineSeparator());
        StringBuilder aVersionsSBUp = new StringBuilder();
        StringBuilder bVersionsSBDown = new StringBuilder();

        aVersionsSBUp.append("Upgrade:").append(System.lineSeparator());
        bVersionsSBDown.append("Downgrade:").append(System.lineSeparator());
        for (CompareData data : compareDataListLess) {
            aVersionsSBUp.append(data.getBVersion());
            bVersionsSBDown.append(data.getAVersion());
        }
        aVersionsSBUp.append(System.lineSeparator()).append("------------------------------").append(System.lineSeparator());
        bVersionsSBDown.append(System.lineSeparator()).append("------------------------------").append(System.lineSeparator()).append("Deploy:").append(System.lineSeparator());

        for (CompareData data : compareDataListX) {
            if (data.serviceAVersion.equals(NA))
                aVersionsSBDown.append(data.getBVersion());
            else
                bVersionsSBDown.append(data.getAVersion());
        }


        model.addAttribute("resultCommom", allDataSB.toString());
        model.addAttribute("resultAToB", aVersionsSBUp.append(aVersionsSBDown).toString());
        model.addAttribute("resultBToA", bVersionsSBUp.append(bVersionsSBDown).toString());
        model.addAttribute("resultMajor", majorVersionsSB.toString());

        return "stand_compare_page";
    }
}
