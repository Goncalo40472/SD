package edu.ufp.inf.sd.rmi.projeto.client.game.terrain;

public class City extends Base {
	public City() {
		name = "City";
	}
	public boolean building() {return true;}
	public double speed() {return 1;}
	public double defense() {return 1.3;}
}
