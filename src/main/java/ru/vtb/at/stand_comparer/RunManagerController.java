package ru.vtb.at.stand_comparer;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.vtb.at.mappers.DPMapper;

import java.util.HashMap;
import java.util.Map;


@Controller
public class RunManagerController {

    @Autowired
    DPMapper dpMapper;

    @GetMapping("/manageRuns")
    public String runManagerPage(Model model) {
        return "manage_runs";
    }

    @GetMapping("/manageRuns/auth")
    public String runManagerPageTestAuth(Model model, String email, String password) {
        model.addAttribute("email", email);
        model.addAttribute("password", password);
//        Response authResponse = RestAssured.given()
//                .relaxedHTTPSValidation()
//                .contentType(ContentType.JSON)
//                .body("{\"username\" : \"\", \"password\" : \"\"}")
//                .post("https://sfer.inno.local/api/aut/login");
        return "manage_runs";
    }

    @GetMapping(value = "/manageRuns/doManage", params = "action=getData")
    public String runManagerPageGetData(Model model, String teamName, String jobName, Integer runsNum) {
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        dpMapper.getFailedTestFor(teamName, jobName, runsNum);


        Map<String, String> cookies = new HashMap<>();
        cookies.put("ACCESS_TOKEN", "");
        cookies.put("REFRESH_TOKEN", "");
        RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .relaxedHTTPSValidation()
                .cookies(cookies);

        return "manage_runs";
    }

    @GetMapping(value = "/manageRuns/doManage", params = "action=rerun")
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


