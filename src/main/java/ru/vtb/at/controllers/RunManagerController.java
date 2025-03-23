package ru.vtb.at.controllers;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vtb.at.mappers.DPMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class RunManagerController {

    @Autowired
    DPMapper dpMapper;

    @ModelAttribute("jobNames")
    public List<String> jobNames() {
        return dpMapper.getJobNames();
    }

    @ModelAttribute("teamNames")
    public List<String> teamNames() {
        return dpMapper.getTeamNames();
    }

    @GetMapping("/manageRuns")
    public String runManagerPage(Model model) {
        model.addAttribute("runsNumber", 1);
        return "manage_runs";
    }

    @PostMapping("/manageRuns/auth")
    public String runManagerPageTestAuth(String email, String password) {
        System.out.println(email);
//        Response authResponse = RestAssured.given()
//                .relaxedHTTPSValidation()
//                .contentType(ContentType.JSON)
//                .body("{\"username\" : \"\", \"password\" : \"\"}")
//                .post("https://sfer.inno.local/api/aut/login");
        return "redirect:/manageRuns";
    }

    @PostMapping(value = "/manageRuns/doManage", params = "action=getData")
    public String runManagerPageGetData(Model model, String teamName, String jobName, Integer runsNumber) {
        teamName = teamName.trim();
        jobName = jobName.trim();
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        model.addAttribute("runsNumber", runsNumber);
        try {
            List<String> tags = dpMapper.getFailedTestsFor(teamName, jobName, runsNumber);
            StringBuilder sb = new StringBuilder();
            tags.forEach(t -> sb.append(t).append(" "));
            model.addAttribute("data", sb.toString());
        } catch (Exception e) {
            model.addAttribute("data", e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return "manage_runs";
        }

        return "manage_runs";
    }

    @PostMapping(value = "/manageRuns/doManage", params = "action=formatData")
    public String runManagerPageGetData(Model model, String teamName, String jobName, Integer runsNumber, String data) {
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        model.addAttribute("runsNumber", runsNumber);

        if (Strings.isNotEmpty(data) && !data.contains("@TmsLink")) {
            StringBuilder sb = new StringBuilder();
            for (String id : data.split(" ")) {
                if (!sb.isEmpty())
                    sb.append(" or ");
                sb.append("@TmsLink=").append(id);
            }
            model.addAttribute("data", sb.toString());
        } else
            model.addAttribute("data", data);

        return "manage_runs";
    }

    @PostMapping(value = "/manageRuns/doManage", params = "action=rerun")
    public String runManagerRunTests(String testList) {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("ACCESS_TOKEN", "");
        cookies.put("REFRESH_TOKEN", "");
//        String.format("{\\\"parameter\\\":[\n" +
//                "{\\\"name\\\":\\\"TAGS\\\",\\\"value\\\":\\\"%s\\\"},\n" +
//                "{\\\"name\\\":\\\"ENVIRONMENT_SETTINGS\\\",\\\"value\\\":\\\"%s\\\"},\n" +
//                "{\\\"name\\\":\\\"FORK_COUNT\\\",\\\"value\\\":\\\"%d\\\"},\n" +
//                "{\\\"name\\\":\\\"EXPORT_ALLURE_RESULTS\\\",\\\"value\\\":%b},\n" +
//                "{\\\"name\\\":\\\"TEAM_NAME\\\",\\\"value\\\":\\\"%s\\\"}]\n" +
//                "}", tagsString, envSettingsPath, forkCount, exportAllureResults, teamName);
        RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .relaxedHTTPSValidation()
                .cookies(cookies);
        JsonObject jsonObject = new JsonObject();
        return "manage_runs";
    }

}


