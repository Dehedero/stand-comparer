package ru.vtb.at.stand_comparer;

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
        return "manage_runs";
    }

    @GetMapping("/manageRuns/getRunsData")
    public String runManagerPageGetData(Model model, String teamName, String jobName) {
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        dpMapper.getFailedTestFor(teamName, jobName);

        Response authResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body("{\"username\" : \"\", \"password\" : \"\"}") //TODO Вытягивать откуда-то логин и пароль
                .post("https://sfer.inno.local/api/aut/login");
        //TODO завезти GSON в pom
        Map<String, String> cookies = new HashMap<>();
        cookies.put("ACCESS_TOKEN", "");
        cookies.put("REFRESH_TOKEN", "");
        RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .relaxedHTTPSValidation()
                .cookies(cookies);

        return "manage_runs";
    }

    @GetMapping("/manageRuns/getRunsData")
    public String runManagerRunTests(Model model, String teamName, String jobName) {
        model.addAttribute("teamName", teamName);
        model.addAttribute("jobName", jobName);
        dpMapper.getFailedTestFor(teamName, jobName);

        Response authResponse = RestAssured.given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body("{\"username\" : \"\", \"password\" : \"\"}") //TODO Вытягивать откуда-то логин и пароль
                .post("https://sfer.inno.local/api/aut/login");
        //TODO завезти GSON в pom
        Map<String, String> cookies = new HashMap<>();
        cookies.put("ACCESS_TOKEN", "");
        cookies.put("REFRESH_TOKEN", "");
        RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .relaxedHTTPSValidation()
                .cookies(cookies);

        return "manage_runs";
    }

}


