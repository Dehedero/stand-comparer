package ru.vtb.at.stand_comparer;


import io.restassured.RestAssured;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.vtb.at.stand_comparer.CompareData.NA;

/**
 * Класс для сравнения стендов (stand) на основе их версий сервисов.
 * Класс предоставляет методы для получения данных стендов, обработки версий сервисов,
 * сравнения различий между стендами.
 */
public class StandComparer implements Comparator<CompareData> {

    private final String BASE_URI = "http://k8s-versions-os.%s.innodev.local";

    /**
     * Получает "сырые" данные (HTML) со стенда по указанному имени.
     *
     * @param stand имя стенда, например, "stand1" или "stand2".
     * @return HTML-строка, содержащая данные о версиях сервисов.
     */
    public String getRawData(String stand) {
        return RestAssured
                .given()
                .baseUri(String.format(BASE_URI, stand))
                .basePath("/versions")
                .get()
                .asString();
    }

    /**
     * Преобразует HTML-данные стенда в карту, где ключ — имя сервиса,
     * а значение — версия сервиса.
     *
     * @param standData HTML-строка с данными о версиях сервисов.
     * @return Сортированная карта (TreeMap), где ключ — имя сервиса, значение — его версия.
     */
    public Map<String, String> getServicesMap(String standData) {
        Document document = Jsoup.parse(standData);
        Element mainTable = document.getElementById("mainTable");
        if (mainTable != null) {
            return new TreeMap<>(mainTable.select("tr").stream()
                    .filter(row -> row.select("td").size() >= 2)
                    .collect(Collectors.toMap(
                            row -> row.select("td").get(0).text().trim(),
                            row -> row.select("td").get(1).text().trim()
                    )));
        }
        return new TreeMap<>();
    }

    /**
     * Преобразует данные из csv файла в карту, где ключ — имя сервиса,
     * а значение — версия сервиса.
     *
     * @param file полученные в запросе файл.
     * @return Сортированная карта (TreeMap), где ключ — имя сервиса, значение — его версия.
     */
    public Map<String, String> getServicesMap(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty())
            return new TreeMap<>();
        byte[] fileBytes = file.getBytes();
        String[] fileRows = new String(fileBytes).split("[\r\n]{1,2}");
        Map<String, String> serviceFileMap = new HashMap<>();
        String[] splitedRow;
        for (String row : fileRows) {
            if (row.matches("^[.\\d]*$"))
                continue;
            splitedRow = row.split("_");
            serviceFileMap.put(splitedRow[0], splitedRow[1]);
        }

        return serviceFileMap;
    }

    /**
     * Сравнивает версии сервисов между двумя стендами и возвращает различия.
     *
     * @param firstStandMap  карта сервисов и их версий для первого стенда.
     * @param secondStandMap карта сервисов и их версий для второго стенда.
     * @return Карта, где ключ — имя сервиса, а значение — массив из двух элементов:
     * версия сервиса на первом стенде и версия на втором стенде.
     */
    public Map<String, String[]> getStandsDifferences(Map<String, String> firstStandMap, Map<String, String> secondStandMap) {
        Set<String> allServices = Stream.concat(firstStandMap.keySet().stream(), secondStandMap.keySet().stream())
                .collect(Collectors.toSet());
        return allServices.stream()
                .filter(service -> {
                    String firstVersion = firstStandMap.get(service);
                    String secondVersion = secondStandMap.get(service);
                    return firstVersion == null || !firstVersion.equals(secondVersion);
                })
                .collect(Collectors.toMap(
                        service -> service,
                        service -> new String[]{firstStandMap.get(service), secondStandMap.get(service)}
                ));
    }

    /**
     * Сравнивает две версии сервисов.
     *
     * @param firstVersion  версия сервиса на первом стенде.
     * @param secondVersion версия сервиса на втором стенде.
     * @return Символ сравнения:
     * - "<" — если первая версия меньше второй;
     * - ">" — если первая версия больше второй;
     * - "=" — если версии равны.
     */
    public String compareVersions(String firstVersion, String secondVersion) {
        if (firstVersion.equals(NA) || secondVersion.equals(NA))
            return "x";

        String[] firstParts = firstVersion.split("\\.");
        String[] secondParts = secondVersion.split("\\.");

        int maxLength = Math.max(firstParts.length, secondParts.length);

        for (int i = 0; i < maxLength; i++) {
            String firstPart = i < firstParts.length ? firstParts[i] : "0";
            String secondPart = i < secondParts.length ? secondParts[i] : "0";
            try {
                int num1 = Integer.parseInt(firstPart);
                int num2 = Integer.parseInt(secondPart);

                if (num1 < num2) return "<";
                if (num1 > num2) return ">";
            } catch (NumberFormatException e) {
                int result = firstPart.compareTo(secondPart);
                if (result < 0) return "<";
                if (result > 0) return ">";
            }
        }
        return "=";
    }

    @Override
    public int compare(CompareData A, CompareData B) {
        if ((A.comparison.equals("x") && !B.comparison.equals("x")) ||
                (A.comparison.equals(">") && B.comparison.equals("<")))
            return 1;
        if (A.comparison.equals(B.comparison))
            return 0;
        return -1;
    }
}

