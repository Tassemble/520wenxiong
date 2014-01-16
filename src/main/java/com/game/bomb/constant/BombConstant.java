package com.game.bomb.constant;

import java.util.HashMap;
import java.util.Map;

public class BombConstant {

	public static final int							NOT_ENOUGH_HEART_CODE			= 99;	// 20个换取1个红心

	public static final int							EXCHANGE_INGOT_TO_HEART_UNIT	= 20;	// 20个换取1个红心

	
	
	//元宝换取红心
	public static final HashMap<Integer, Integer>	EXCHANGE_INGOT_TO_HEART_MAPPING								= new HashMap<Integer, Integer>()
    {
		private static final long	serialVersionUID	= -3703929355568521604L;
		{
			put(20, 30);
			put(40, 70);
			put(60, 110);
		}
	};
	
	
	
	//元宝换金币
	public static final HashMap<Integer, Integer>	EXCHANGE_INGOT_TO_GOLD_MAPPING								= new HashMap<Integer, Integer>()
		    {
				private static final long	serialVersionUID	= -3703929355568521604L;
				{
					put(2, 20);
					put(4, 50);
					put(6, 120);
					put(12, 300);
				}
			};

			//上线的时候要注意
	public static final int	CONSTANT_FULL_HEART	= 1000;
			
			

}
