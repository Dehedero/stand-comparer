package ru.vtb.at.stand_comparer;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/manageRuns")
    public String runManagerPage(Model model) {
        return "manage_runs";
    }

    @GetMapping("/manageRuns/testLogPass")
    public String runManagerPageTestAuth(Model model, String email, String password) {
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        return "manage_runs";
    }

    @GetMapping("/manageRuns/getRunsData")
    public String runManagerPageGetData(Model model, String teamName, String jobName) {
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        return "manage_runs";
    }

    @GetMapping("/compareStands/compare")
    public String compareStands(Model model, String standA, String standB) {
        model.addAttribute("standA", standA);
        model.addAttribute("standB", standB);
        String firstStandData;
        String secondStandData;

        try {
            firstStandData = standComparer.getRawData(standA);
            secondStandData = standComparer.getRawData(standB);
        } catch (Exception e) {
            model.addAttribute("resultCommom", e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return "stand_compare_page";
        }

        Map<String, String> firstServicesMap = standComparer.getServicesMap(firstStandData);
        Map<String, String> secondServicesMap = standComparer.getServicesMap(secondStandData);
        Map<String, String[]> standsDifferences = standComparer.getStandsDifferences(firstServicesMap, secondServicesMap);

        int serviceColumnWidth = 60;
        String formatHeader = "%-" + serviceColumnWidth + "s%-" + serviceColumnWidth + "s";
        String headers = String.format(formatHeader, standA.replaceAll("\\..+", ""), standB.replaceAll("\\..+", ""));

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
            if (comparison.equals("x"))
                compareDataListX.add(data);
            else if (comparison.equals(">"))
                compareDataListBigger.add(data);
            else
                compareDataListLess.add(data);
        }
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
