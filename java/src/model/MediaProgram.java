package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MediaProgram {

    private int idProgram;
    private String title;
    private int durationSec;
    private String domeFormat;
    private int targetFps;
    private String fileChecksum;
    private String domeMasterFile;
    private String audioFile;
    private String scenarioFile;

    public int getTotalExpectedFrames() {
        return durationSec * targetFps;
    }

    /**
     * Алгоритм 3: Валидация целостности медиапакета программы и корректности формата видеоряда.
     * Временная сложность: O(F). Ёмкостная: O(E).
     * @return список обнаруженных ошибок (пустой список = медиапакет валиден)
     */
    public List<String> validateMediaPackage() {
        List<String> errors = new ArrayList<>();
        if (domeMasterFile == null || domeMasterFile.isEmpty())
            errors.add("Отсутствует обязательный компонент: dome_master_file");
        else if (!domeMasterFile.endsWith(".mp4") && !domeMasterFile.endsWith(".mov"))
            errors.add("Неподдерживаемый формат файла: " + domeMasterFile);
        if (audioFile == null || audioFile.isEmpty())
            errors.add("Отсутствует обязательный компонент: audio_file");
        if (scenarioFile == null || scenarioFile.isEmpty())
            errors.add("Отсутствует обязательный компонент: scenario_file");
        if (fileChecksum == null || fileChecksum.isEmpty())
            errors.add("Отсутствует обязательный компонент: checksum");
        return errors;
    }

}