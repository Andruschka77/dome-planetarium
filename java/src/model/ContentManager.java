package model;

public class ContentManager extends Employee {

    public ContentManager(
            int id,
            String fullName,
            String phone
    ) {
        super(id, fullName, phone);
    }

    @Override
    public String performDuty() {
        return "Загрузка медиапакета на медиасервер, перекодирование при необходимости";
    }

}