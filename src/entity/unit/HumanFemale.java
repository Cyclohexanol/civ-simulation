package entity.unit;

public class HumanFemale extends Human{
	
	public HumanFemale(int id, Family family, int age, int birthday) {
		super(id, Sex.FEMALE, COLOR_HUMAN_FEMALE, family, age, birthday);
	}

}
