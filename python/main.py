import math

def verify_cluster_calibration(projectors_calibration, tolerance_threshold=1.5):
    """
    Алгоритм 1: Проверка оптического сведения кластера проекторов и расчет параметров коррекции.
    Временная сложность: O(N * M), где N - количество проекторов, M - число контрольных точек сетки.
    Емкостная сложность: O(N) для формирования отчета по кластеру.
    
    :param projectors_calibration: список словарей с калибровочными данными проекторов
    :param tolerance_threshold: допустимый предел среднеквадратичной ошибки в пикселях (px)
    :return: кортеж (cluster_report, is_cluster_ready, total_recalibration_needed)
    """
    cluster_report = []
    total_recalibration_needed = 0
    is_cluster_ready = True
    
    i = 0
    while i < len(projectors_calibration):
        proj_data = projectors_calibration[i]
        proj_id = proj_data.get("id_projector")
        model = proj_data.get("model", "Unknown")
        grid_points = proj_data.get("grid_points", [])
        
        sum_squared_error = 0.0
        max_deviation = 0.0
        points_count = len(grid_points)
        
        j = 0
        while j < points_count:
            pt = grid_points[j]
            # Фактические измеренные камерой координаты точки на куполе
            actual_x = pt.get("actual_x", 0.0)
            actual_y = pt.get("actual_y", 0.0)
            # Эталонные расчетные координаты сферической развертки
            target_x = pt.get("target_x", 0.0)
            target_y = pt.get("target_y", 0.0)
            
            # Евклидово расстояние (отклонение точки)
            dx = actual_x - target_x
            dy = actual_y - target_y
            dist = math.sqrt(dx * dx + dy * dy)
            
            sum_squared_error += dist * dist
            if dist > max_deviation:
                max_deviation = dist
            
            j += 1
        
        # Расчет среднеквадратичной ошибки (RMS)
        if points_count > 0:
            rms_error = math.sqrt(sum_squared_error / points_count)
        else:
            rms_error = 0.0
        
        # Проверка порога оптической погрешности
        if rms_error > tolerance_threshold:
            status = "Требуется юстировка"
            recalibration_needed = True
            blend_mask_width = 0
            total_recalibration_needed += 1
            is_cluster_ready = False
        else:
            status = "В норме"
            recalibration_needed = False
            # Динамический расчет ширины зоны сшивки (blending) в зависимости от точности
            blend_mask_width = int(64 + (tolerance_threshold - rms_error) * 20)
        
        proj_summary = {
            "id_projector": proj_id,
            "model": model,
            "points_checked": points_count,
            "rms_error_px": round(rms_error, 2),
            "max_deviation_px": round(max_deviation, 2),
            "blend_mask_width_px": blend_mask_width,
            "status": status,
            "recalibration_needed": recalibration_needed
        }
        cluster_report.append(proj_summary)
        i += 1
        
    return cluster_report, is_cluster_ready, total_recalibration_needed


def analyze_session_telemetry(telemetry_logs, total_expected_frames):
    """
    Алгоритм 2: Статистический анализ телеметрии и показателей качества сеанса.
    Временная сложность: O(K), где K - количество записей в журнале телеметрии/инцидентов.
    Емкостная сложность: O(1) (агрегация метрик в результирующий словарь фиксированного размера).
    
    :param telemetry_logs: список словарей с логами инцидентов/аномалий показа
    :param total_expected_frames: номинальное число кадров полнокупольного фильма
    :return: словарь с аналитическими метриками качества показа
    """
    if total_expected_frames <= 0:
        return {
            "stability_index_percent": 0.0,
            "dropped_frame_rate": 0.0,
            "hardware_issues_ratio": 0.0,
            "sync_jitter_ratio": 0.0,
            "software_errors_ratio": 0.0,
            "total_incidents": 0,
            "session_verdict": "Некорректная длительность сеанса"
        }
    
    hw_overheat_count = 0
    sync_jitter_count = 0
    decoder_error_count = 0
    total_dropped_frames = 0
    
    k = 0
    while k < len(telemetry_logs):
        log_entry = telemetry_logs[k]
        event_type = log_entry.get("type", "").lower()
        dropped = log_entry.get("dropped_frames", 0)
        total_dropped_frames += dropped
        
        # Классификация инцидентов
        if event_type == "thermal_warning":
            hw_overheat_count += 1
        elif event_type == "sync_jitter":
            sync_jitter_count += 1
        else:
            decoder_error_count += 1
            
        k += 1
        
    total_incidents = len(telemetry_logs)
    
    # Расчет долей инцидентов
    if total_incidents > 0:
        hw_ratio = round(hw_overheat_count / total_incidents, 3)
        sync_ratio = round(sync_jitter_count / total_incidents, 3)
        sw_ratio = round(decoder_error_count / total_incidents, 3)
    else:
        hw_ratio = 0.0
        sync_ratio = 0.0
        sw_ratio = 0.0
        
    # Процент выпавших кадров
    dropped_rate = round((total_dropped_frames / total_expected_frames) * 100.0, 3)
    # Индекс стабильности показа (от 100% отнимаем процент деградации)
    stability_index = round(max(0.0, 100.0 - dropped_rate - (total_incidents * 0.5)), 2)
    
    if stability_index >= 98.0:
        verdict = "Показ эталонного качества"
    elif stability_index >= 90.0:
        verdict = "Показ удовлетворительного качества"
    else:
        verdict = "Сеанс с критическими сбоями"
        
    return {
        "total_incidents": total_incidents,
        "total_dropped_frames": total_dropped_frames,
        "dropped_frame_rate_percent": dropped_rate,
        "stability_index_percent": stability_index,
        "hw_issues_count": hw_overheat_count,
        "sync_jitter_count": sync_jitter_count,
        "sw_errors_count": decoder_error_count,
        "hw_issues_ratio": hw_ratio,
        "sync_jitter_ratio": sync_ratio,
        "sw_errors_ratio": sw_ratio,
        "session_verdict": verdict
    }


def get_mock_data():
    """
    Формирование тестовых данных, моделирующих сущности логической ER-модели:
    Projector, Calibration_Profile, Show_Session, Telemetry_Incident.
    """
    # 5 проекторов купольного кластера (1 зенитный + 4 периферийных сектора)
    projectors_calibration_data = [
        {
            "id_projector": 1,
            "model": "Barco F80-4K9 (Зенит)",
            "grid_points": [
                {"actual_x": 1920.2, "actual_y": 1080.1, "target_x": 1920.0, "target_y": 1080.0},
                {"actual_x": 1500.8, "actual_y": 800.4, "target_x": 1500.0, "target_y": 800.0},
                {"actual_x": 2340.5, "actual_y": 800.3, "target_x": 2340.0, "target_y": 800.0},
                {"actual_x": 1920.6, "actual_y": 1400.9, "target_x": 1920.0, "target_y": 1400.0}
            ]
        },
        {
            "id_projector": 2,
            "model": "Barco F80-4K9 (Север)",
            "grid_points": [
                {"actual_x": 1921.1, "actual_y": 1081.3, "target_x": 1920.0, "target_y": 1080.0},
                {"actual_x": 1501.9, "actual_y": 801.5, "target_x": 1500.0, "target_y": 800.0},
                {"actual_x": 2341.2, "actual_y": 801.0, "target_x": 2340.0, "target_y": 800.0},
                {"actual_x": 1921.4, "actual_y": 1401.8, "target_x": 1920.0, "target_y": 1400.0}
            ]
        },
        {
            "id_projector": 3,
            "model": "Barco F80-4K9 (Восток - смещен температурным дрейфом)",
            "grid_points": [
                {"actual_x": 1923.8, "actual_y": 1084.2, "target_x": 1920.0, "target_y": 1080.0},
                {"actual_x": 1504.5, "actual_y": 804.1, "target_x": 1500.0, "target_y": 800.0},
                {"actual_x": 2345.1, "actual_y": 803.9, "target_x": 2340.0, "target_y": 800.0},
                {"actual_x": 1924.0, "actual_y": 1404.7, "target_x": 1920.0, "target_y": 1400.0}
            ]
        },
        {
            "id_projector": 4,
            "model": "Barco F80-4K9 (Юг)",
            "grid_points": [
                {"actual_x": 1920.3, "actual_y": 1080.2, "target_x": 1920.0, "target_y": 1080.0},
                {"actual_x": 1500.4, "actual_y": 800.2, "target_x": 1500.0, "target_y": 800.0},
                {"actual_x": 2340.6, "actual_y": 800.5, "target_x": 2340.0, "target_y": 800.0},
                {"actual_x": 1920.5, "actual_y": 1400.3, "target_x": 1920.0, "target_y": 1400.0}
            ]
        },
        {
            "id_projector": 5,
            "model": "Barco F80-4K9 (Запад)",
            "grid_points": [
                {"actual_x": 1920.7, "actual_y": 1080.8, "target_x": 1920.0, "target_y": 1080.0},
                {"actual_x": 1501.0, "actual_y": 800.9, "target_x": 1500.0, "target_y": 800.0},
                {"actual_x": 2340.9, "actual_y": 800.7, "target_x": 2340.0, "target_y": 800.0},
                {"actual_x": 1921.1, "actual_y": 1401.0, "target_x": 1920.0, "target_y": 1400.0}
            ]
        }
    ]
    
    # Лог телеметрии полнокупольного сеанса (продолжительность 45 мин = 162 000 кадров при 60 fps)
    total_expected_frames = 162000
    telemetry_logs_data = [
        {"timestamp": "12:15:20", "type": "thermal_warning", "id_projector": 3, "dropped_frames": 0, "desc": "Температура лазера 68C"},
        {"timestamp": "12:24:10", "type": "sync_jitter", "id_projector": 2, "dropped_frames": 4, "desc": "Сдвиг тактового импульса Genlock на 12 мс"},
        {"timestamp": "12:35:45", "type": "decoder_error", "id_projector": 3, "dropped_frames": 18, "desc": "Буферизация Dome Master видеокадра"},
        {"timestamp": "12:48:02", "type": "sync_jitter", "id_projector": 5, "dropped_frames": 2, "desc": "Сетевой джиттер медиасервера"}
    ]
    
    return projectors_calibration_data, telemetry_logs_data, total_expected_frames


def main():
    print("=" * 70)
    print("ПРОЦЕДУРНАЯ ПРОГРАММА УПРАВЛЕНИЯ КУПОЛЬНОЙ ПРОЕКЦИОННОЙ СИСТЕМОЙ")
    print("=" * 70)
    
    projectors_data, telemetry_data, total_frames = get_mock_data()
    
    # -------------------------------------------------------------------------
    # ТЕСТ АЛГОРИТМА 1: Проверка оптического сведения кластера проекторов
    # -------------------------------------------------------------------------
    print("\n[ТЕСТ 1] Запуск алгоритма проверки оптической юстировки кластера...")
    tolerance = 2.0  # допустимый порог RMS в пикселях
    cluster_report, is_ready, recalib_count = verify_cluster_calibration(projectors_data, tolerance)
    
    print(f"Порог допустимой погрешности сведения: {tolerance} px")
    print(f"Готовность кластера к запуску сеанса: {'ГОТОВ' if is_ready else 'НЕ ГОТОВ (требуется юстировка)'}")
    print(f"Количество проекторов, требующих повторной юстировки: {recalib_count}")
    print("\nДетализация по проекторам кластера:")
    for rep in cluster_report:
        print(f"  Проектор #{rep['id_projector']} ({rep['model']}): "
              f"RMS={rep['rms_error_px']} px, MaxDev={rep['max_deviation_px']} px, "
              f"Зона сшивки={rep['blend_mask_width_px']} px -> [{rep['status']}]")
        
    # -------------------------------------------------------------------------
    # ТЕСТ АЛГОРИТМА 2: Статистический анализ телеметрии и качества сеанса
    # -------------------------------------------------------------------------
    print("\n" + "-" * 70)
    print("[ТЕСТ 2] Запуск алгоритма анализа телеметрии прошедшего сеанса...")
    stats = analyze_session_telemetry(telemetry_data, total_frames)
    
    print(f"Номинальное число кадров сеанса: {total_frames}")
    print(f"Суммарно зафиксировано инцидентов: {stats['total_incidents']}")
    print(f"Суммарно выпавших кадров: {stats['total_dropped_frames']} ({stats['dropped_frame_rate_percent']}%)")
    print(f"Итоговый индекс стабильности показа: {stats['stability_index_percent']}%")
    print(f"Вердикт системы: {stats['session_verdict']}")
    print("Структура зафиксированных сбоев:")
    print(f"  - Аппаратные предупреждения (нагрев): {stats['hw_issues_count']} (доля {stats['hw_issues_ratio']})")
    print(f"  - Джиттер кадровой синхронизации: {stats['sync_jitter_count']} (доля {stats['sync_jitter_ratio']})")
    print(f"  - Ошибки декодирования потока: {stats['sw_errors_count']} (доля {stats['sw_errors_ratio']})")
    print("=" * 70)


if __name__ == "__main__":
    main()
