import model.*;
import service.CalibrationRule;
import service.StandardBlendRuleImpl;
import service.StrictBlendRuleImpl;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("ООП-ПРОГРАММА УПРАВЛЕНИЯ КУПОЛЬНОЙ ПРОЕКЦИОННОЙ СИСТЕМОЙ");
        System.out.println("=".repeat(70));

        // ----- Зал -----
        Hall hall = new Hall(1, "Главный купол", 23.0, "перфорированный", 150, "г. Москва");

        // ----- 5 проекторов кластера -----
        Projector p1 = new Projector(1, hall, "Barco F80-4K9 (Зенит)",   "SN-001", 3840,2160,30000,"192.168.1.1","active");
        Projector p2 = new Projector(2, hall, "Barco F80-4K9 (Север)",   "SN-002", 3840,2160,30000,"192.168.1.2","active");
        Projector p3 = new Projector(3, hall, "Barco F80-4K9 (Восток - смещен температурным дрейфом)", "SN-003", 3840,2160,30000,"192.168.1.3","active");
        Projector p4 = new Projector(4, hall, "Barco F80-4K9 (Юг)",      "SN-004", 3840,2160,30000,"192.168.1.4","active");
        Projector p5 = new Projector(5, hall, "Barco F80-4K9 (Запад)",   "SN-005", 3840,2160,30000,"192.168.1.5","active");

        p1.setPoweredOn(true); p1.setLaserTempCelsius(58); p1.setMediaServerLink(true);
        p2.setPoweredOn(true); p2.setLaserTempCelsius(61); p2.setMediaServerLink(true);
        p3.setPoweredOn(true); p3.setLaserTempCelsius(68); p3.setMediaServerLink(true);
        p4.setPoweredOn(true); p4.setLaserTempCelsius(59); p4.setMediaServerLink(true);
        p5.setPoweredOn(true); p5.setLaserTempCelsius(62); p5.setMediaServerLink(true);

        List<Projector> projectorList = Arrays.asList(p1, p2, p3, p4, p5);

        // ----- Калибровочные профили с точками сетки -----
        CalibrationProfile cp1 = new CalibrationProfile(1, p1, "2026-11-20");
        cp1.addGridPoint(new CalibrationGridPoint(1, 1, cp1, 1920.0, 1080.0, 1920.2, 1080.1));
        cp1.addGridPoint(new CalibrationGridPoint(2, 2, cp1, 1500.0, 800.0, 1500.8, 800.4));
        cp1.addGridPoint(new CalibrationGridPoint(3, 3, cp1, 2340.0, 800.0, 2340.5, 800.3));
        cp1.addGridPoint(new CalibrationGridPoint(4, 4, cp1, 1920.0, 1400.0, 1920.6, 1400.9));

        CalibrationProfile cp2 = new CalibrationProfile(2, p2, "2026-11-20");
        cp2.addGridPoint(new CalibrationGridPoint(5, 1, cp2, 1920.0, 1080.0, 1921.1, 1081.3));
        cp2.addGridPoint(new CalibrationGridPoint(6, 2, cp2, 1500.0, 800.0, 1501.9, 801.5));
        cp2.addGridPoint(new CalibrationGridPoint(7, 3, cp2, 2340.0, 800.0, 2341.2, 801.0));
        cp2.addGridPoint(new CalibrationGridPoint(8, 4, cp2, 1920.0, 1400.0, 1921.4, 1401.8));

        CalibrationProfile cp3 = new CalibrationProfile(3, p3, "2026-11-20");
        cp3.addGridPoint(new CalibrationGridPoint(9,  1, cp3, 1920.0, 1080.0, 1923.8, 1084.2));
        cp3.addGridPoint(new CalibrationGridPoint(10, 2, cp3, 1500.0, 800.0, 1504.5, 804.1));
        cp3.addGridPoint(new CalibrationGridPoint(11, 3, cp3, 2340.0, 800.0, 2345.1, 803.9));
        cp3.addGridPoint(new CalibrationGridPoint(12, 4, cp3, 1920.0, 1400.0, 1924.0, 1404.7));

        CalibrationProfile cp4 = new CalibrationProfile(4, p4, "2026-11-20");
        cp4.addGridPoint(new CalibrationGridPoint(13, 1, cp4, 1920.0, 1080.0, 1920.3, 1080.2));
        cp4.addGridPoint(new CalibrationGridPoint(14, 2, cp4, 1500.0, 800.0, 1500.4, 800.2));
        cp4.addGridPoint(new CalibrationGridPoint(15, 3, cp4, 2340.0, 800.0, 2340.6, 800.5));
        cp4.addGridPoint(new CalibrationGridPoint(16, 4, cp4, 1920.0, 1400.0, 1920.5, 1400.3));

        CalibrationProfile cp5 = new CalibrationProfile(5, p5, "2026-11-20");
        cp5.addGridPoint(new CalibrationGridPoint(17, 1, cp5, 1920.0, 1080.0, 1920.7, 1080.8));
        cp5.addGridPoint(new CalibrationGridPoint(18, 2, cp5, 1500.0, 800.0, 1501.0, 800.9));
        cp5.addGridPoint(new CalibrationGridPoint(19, 3, cp5, 2340.0, 800.0, 2340.9, 800.7));
        cp5.addGridPoint(new CalibrationGridPoint(20, 4, cp5, 1920.0, 1400.0, 1921.1, 1401.0));

        List<CalibrationProfile> profiles = Arrays.asList(cp1, cp2, cp3, cp4, cp5);

        // ----- Медиапрограмма и сеанс -----
        MediaProgram program = new MediaProgram(1, "Эволюция Вселенной", 2700,
                "Dome Master 8K", 60, "d41d8cd98f00b204e9800998ecf8427e",
                "cosmos_evolution_8k.mp4", "soundtrack_7_1.wav", "timeline_script.json");

        Employee operator = new Operator(1, "Иванов И.И.", "+7-999-001");
        ShowSession session = new ShowSession(1, "2026-11-20 14:00",
                hall, program, operator, 120, "completed");

        // ----- Инциденты телеметрии -----
        List<TelemetryIncident> incidents = Arrays.asList(
                new TelemetryIncident(1, "12:15:20", "thermal_warning", "WARNING", "Температура лазера 68C", 0, p3, session),
                new TelemetryIncident(2, "12:24:10", "sync_jitter", "WARNING", "Сдвиг тактового импульса на 12 мс", 4, p2, session),
                new TelemetryIncident(3, "12:35:45", "decoder_error", "ERROR", "Буферизация Dome Master кадра", 18, p3, session),
                new TelemetryIncident(4, "12:48:02", "sync_jitter", "WARNING", "Сетевой джиттер медиасервера", 2, p5, session)
        );

        // =================================================================
        // ТЕСТ 1: verifyClusterCalibration
        // =================================================================
        System.out.println("\n[ТЕСТ 1] Запуск алгоритма проверки оптической юстировки кластера...");
        double          tolerance    = 2.0;
        CalibrationRule rule         = new StandardBlendRuleImpl();
        CalibrationProfile.ClusterReport calibReport =
                CalibrationProfile.verifyClusterCalibration(profiles, tolerance, rule);

        System.out.println("Порог допустимой погрешности сведения: " + tolerance + " px");
        System.out.println("Готовность кластера к запуску сеанса: "
                + (calibReport.isClusterReady ? "ГОТОВ" : "НЕ ГОТОВ (требуется юстировка)"));
        System.out.println("Количество проекторов, требующих повторной юстировки: "
                + calibReport.totalRecalibrationNeeded);
        System.out.println("\nДетализация по проекторам кластера:");
        for (CalibrationProfile.ProjectorResult r : calibReport.projectorResults) {
            System.out.printf("  Проектор #%d (%s): RMS=%.2f px, MaxDev=%.2f px, " +
                            "Зона сшивки=%d px -> [%s]%n",
                    r.idProjector, r.model, r.rmsErrorPx, r.maxDeviationPx,
                    r.blendMaskWidthPx, r.status);
        }

        // =================================================================
        // ТЕСТ 2: analyzeSessionTelemetry
        // =================================================================
        System.out.println("\n" + "-".repeat(70));
        System.out.println("[ТЕСТ 2] Запуск алгоритма анализа телеметрии прошедшего сеанса...");
        ShowSession.TelemetryStats stats = session.analyzeSessionTelemetry(incidents);

        System.out.println("Номинальное число кадров сеанса: " + session.getTotalExpectedFrames());
        System.out.println("Суммарно зафиксировано инцидентов: " + stats.totalIncidents);
        System.out.printf("Суммарно выпавших кадров: %d (%.3f%%)%n",
                stats.totalDroppedFrames, stats.droppedFrameRatePercent);
        System.out.printf("Итоговый индекс стабильности показа: %.2f%%%n",
                stats.stabilityIndexPercent);
        System.out.println("Вердикт системы: " + stats.sessionVerdict);
        System.out.println("Структура зафиксированных сбоев:");
        System.out.printf("  - Аппаратные предупреждения (нагрев): %d (доля %s)%n",
                stats.hwIssuesCount, stats.hwIssuesRatio);
        System.out.printf("  - Джиттер кадровой синхронизации: %d (доля %s)%n",
                stats.syncJitterCount, stats.syncJitterRatio);
        System.out.printf("  - Ошибки декодирования потока: %d (доля %s)%n",
                stats.swErrorsCount, stats.swErrorsRatio);

        // =================================================================
        // ТЕСТ 3: validateMediaPackage
        // =================================================================
        System.out.println("\n" + "-".repeat(70));
        System.out.println("[ТЕСТ 3] Запуск валидации входящего медиапакета программы...");
        List<String> mediaErrors = program.validateMediaPackage();
        System.out.println("Статус валидации медиапакета: " + (mediaErrors.isEmpty() ? "УСПЕШНО" : "ОШИБКА"));
        System.out.println("Обнаружено критических ошибок: " + mediaErrors.size());
        if (mediaErrors.isEmpty()) {
            System.out.println("  Все компоненты (видео Dome Master, аудио 7.1, сценарий, MD5) верифицированы.");
        } else {
            mediaErrors.forEach(e -> System.out.println("  • " + e));
        }

        // =================================================================
        // ТЕСТ 4: checkProjectorsReadiness
        // =================================================================
        System.out.println("\n" + "-".repeat(70));
        System.out.println("[ТЕСТ 4] Запуск экспресс-диагностики готовности кластера проекторов...");

        List<Integer> notReadyIds = Projector.checkProjectorsReadiness(projectorList);
        System.out.println("Готовность кластера к сеансу: " + (notReadyIds.isEmpty() ? "ГОТОВ" : "НЕ ГОТОВ"));
        System.out.println("Всего проекторов опрошено: " + projectorList.size());
        System.out.println("Проекторов с аппаратными замечаниями: " + notReadyIds.size());

        if (notReadyIds.isEmpty()) {
            System.out.println("  Все проекторы активны, температура лазеров в норме (<70C), линк с сервером OK.");
        } else {
            System.out.println("  Требуют внимания ID: " + notReadyIds);
        }

        // Демонстрация наследования и полиморфизма
        System.out.println("\n" + "-".repeat(70));
        System.out.println("Персонал предсеансового регламента (наследование + полиморфизм):");
        List<Employee> staff = Arrays.asList(
                new Operator(1,"Иванов И.И.","+7-999-001"),
                new Engineer(2,"Петров П.П.","+7-999-002"),
                new ContentManager(3,"Сидорова А.В.","+7-999-003")
        );
        for (Employee emp : staff) {
            System.out.println("  " + emp + " → " + emp.performDuty());
        }

        // Демонстрация второй стратегии (StrictBlendRuleImpl)
        System.out.println("\n" + "-".repeat(70));
        System.out.println("Демонстрация второй реализации правила сшивки (StrictBlendRuleImpl):");

        CalibrationRule strictRule = new StrictBlendRuleImpl();
        CalibrationProfile.ClusterReport strictReport =
                CalibrationProfile.verifyClusterCalibration(profiles, tolerance, strictRule);

        System.out.println("Готовность кластера по прецизионному правилу: "
                + (strictReport.isClusterReady ? "ГОТОВ" : "НЕ ГОТОВ (требуется юстировка)"));
        System.out.println("\n" + "=".repeat(70));
    }

}