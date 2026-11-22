package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Employee {

    private int idEmployee;
    private String fullName;
    private String phone;

    // Каждая роль определяет собственные обязанности
    public abstract String performDuty();

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] " + fullName;
    }

}