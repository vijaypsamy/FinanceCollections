import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainClass {

	public static void main(String[] args) {
		
		String dRandom = "19/JUL/2015";
		String dPayment = "20/JUL/2015";
		try {
			Date d2 = new SimpleDateFormat("dd/MMM/yyyy").parse(dRandom);
			Date d3 = new SimpleDateFormat("dd/MMM/yyyy").parse(dPayment);
			
			int accno=3456;
			double billedAmount=1000;
			double amountReceived=0;
			System.out.println("Sending Data.");
			UpdateTables a = new UpdateTables();
			a.update2(accno, d2, billedAmount, amountReceived, d3);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}

}


