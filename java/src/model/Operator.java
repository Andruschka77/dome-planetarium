package model;

public class Operator extends Employee {

    public Operator(
            int id,
            String fullName,
            String phone
    ) {
        super(id, fullName, phone);
    }

    @Override
    public String performDuty() {
        return "Загрузка сценария, предстартовый прогон, подача команды старта сеанса";
    }

}