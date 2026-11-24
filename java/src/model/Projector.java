package model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Projector {

    private int idProjector;
    private Hall hall;
    private String model;
    private String serialNumber;
    private int resolutionWidth;
    private int resolutionHeight;
    private int maxLumens;
    private String ipAddress;
    private String status;

    // Аппаратный статус (заполняется перед диагностикой)
    private boolean poweredOn;
    private double laserTempCelsius;
    private boolean mediaServerLink;

    public static final double MAX_LASER_TEMP_CELSIUS = 70.0;

    public Projector(
            int idProjector,
            Hall hall,
            String model,
            String serialNumber,
            int resolutionWidth,
            int resolutionHeight,
            int maxLumens,
            String ipAddress,
            String status
    ) {
        this.idProjector = idProjector;
        this.hall = hall;
        this.model = model;
        this.serialNumber = serialNumber;
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.maxLumens = maxLumens;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    /**
     * Алгоритм 4: Проверка аппаратной готовности кластера проекторов к запуску сеанса.
     * Временная сложность: O(N). Ёмкостная: O(N) в худшем случае.
     *
     * @param projectors список проекторов кластера
     * @return список ID проекторов, не готовых к сеансу (пустой — кластер готов)
     */
    public static List<Integer> checkProjectorsReadiness(List<Projector> projectors) {
        List<Integer> notReadyIds = new ArrayList<>();
        for (Projector p : projectors) {
            if (!p.isPoweredOn() || p.getLaserTempCelsius() >= MAX_LASER_TEMP_CELSIUS || !p.isMediaServerLink()) {
                notReadyIds.add(p.getIdProjector());
            }
        }
        return notReadyIds;
    }

}