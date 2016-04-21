package entity.unit;

public class HumanMale extends Human {

	public HumanMale(int id, Family family, int age, int birthday) {
		super(id, Sex.MALE, COLOR_HUMAN_MALE, family, age, birthday);
	}

}
