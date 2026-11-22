package model;

public class Engineer extends Employee {

    public Engineer(
            int id,
            String fullName,
            String phone
    ) {
        super(id, fullName, phone);
    }

    @Override
    public String performDuty() {
        return "Запуск предсеансовой диагностики, юстировка оптических осей, утверждение профиля";
    }

}
