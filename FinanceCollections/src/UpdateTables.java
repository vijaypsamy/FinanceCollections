import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class UpdateTables {
	@SuppressWarnings({ "resource" })
	public void update2(int accno, Date billCyleDate, double billedAmount,
			double amountReceived, Date paymentDate) {
		int p2pDays = 0, st;
		
		try { // --------------------------------------------------------------------
				// Establish connection to database.

			Connection con = DBConnection.getConnection();
			PreparedStatement pst = null;
						
			String strDate = new SimpleDateFormat("dd/MMM/YYYY").format(new Date());
			Date currDate = new SimpleDateFormat("dd/MMM/yyyy").parse(strDate);

			long days = ((long) ((currDate.getTime() - billCyleDate.getTime()) / (24 * 60 * 1000 * 60))-15);
			
			System.out.println("Days elapsed: " + days);
			System.out.println(currDate);
			int a = classification(accno, billCyleDate, billedAmount,
					amountReceived, paymentDate, days,con);
			
			

			System.out.println("Before switch");
			System.out.println("A: " + a);

			switch (a) { // ------------------------------------------------------------
							// Switch case for update, delete and insert.
			case 1: { // update = 1; delete = 2; insert = 3.
				System.out.println("Into Delete");
				String delQuery = "delete from dlqtable where account_number = ?";
				pst = con.prepareStatement(delQuery);
				pst.setInt(1, accno);
				pst.execute();
				pst.close();

			}
				break;
			case 2: {
				System.out.println("Inserting data into dlqtable.");

				st = status(days);

				int flag = 1;
				if (days >= 30) {
					flag = 0;
				}
				String insertQuery = "insert into dlqtable (account_number,days_elapsed, due_amount, status, flag)"
						+ "values(?,?,?,?,?)";
				pst = con.prepareStatement(insertQuery);
				pst.setInt(1, accno);
				pst.setLong(2, days);
				pst.setDouble(3, (billedAmount - amountReceived));
				pst.setInt(4, st);
				pst.setInt(5, flag);
				pst.execute();
				pst.close();

			}
				break;

			case 3: {

				System.out.println("into update");

				pst = con
						.prepareStatement("select p2p_Days from dlqtable where account_number = ?");
				pst.setInt(1, accno);
				ResultSet rs = pst.executeQuery();
				rs.next();
				p2pDays = rs.getInt("p2p_days");

				System.out.println("P@P days: " + p2pDays);
				pst.close();
				
				
				int flag = 1;
				if (days >= 30) {
					flag = 0;
				}

				if (days > p2pDays) {
					st = status(days);

					String updateQuery = "update dlqtable set days_elapsed=?, due_amount = ?, status=?, flag=? where account_number=?";
					pst = con.prepareStatement(updateQuery);

					pst.setLong(1, days);
					pst.setDouble(2, (billedAmount - amountReceived));
					pst.setInt(3, st);
					pst.setInt(4, flag);
					pst.setInt(5, accno);
					pst.execute();
					pst.close();

				} else {

					String updateQuery = "update dlqtable set days_elapsed=?, due_amount = ?, flag=? where account_number=?";
					pst = con.prepareStatement(updateQuery);

					pst.setLong(1, days);
					pst.setDouble(2, (billedAmount - amountReceived));

					pst.setInt(3, flag);
					pst.setInt(4, accno);
					pst.execute();
					pst.close();

				}

			}
				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int classification(int accno, Date billCyleDate,
			double billedAmount, // ---------------- classification() returns an
									// int, whether the row should be
			double amountReceived, Date paymentDate, long days, Connection con) { // updated,
																		// deleted
																		// or
																		// inserted.
		int a = 0;
		String query = "select * from dlqtable where account_number = ?";
		try {
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, accno);
			ResultSet rs = pst.executeQuery();
			boolean state = rs.next();

			if (state == false && (billedAmount - amountReceived) != 0 && (days>0)) {
				a = 2;
			} else if (state == true && (billedAmount - amountReceived) == 0) {
				a = 1;
			} else if (state == true && (billedAmount - amountReceived) != 0 && (days>0)) {
				a = 3;
			}
			while (rs.next()) {
				System.out.println("into resultset");
				System.out.println(rs.getInt("Account_Number"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	public int status(long days) { // ------------------------------------
									// status() returns an int which is to be
									// filled in
		int a = 0; // the "status" column in "DLQTABLE" table.
		if (days >= 1 && days <= 3) {
			a = 1;
		} else if (days >= 4 && days <= 6) {
			a = 4;
		} else if (days >= 7 && days <= 10) {
			a = 7;
		} else if (days >= 11 && days <= 17) {
			a = 11;
		} else if (days >= 18 && days <= 20) {
			a = 18;
		} else if (days == 21) {
			a = 21;
		} else if (days >= 22 && days <= 24) {
			a = 22;
		} else if (days >= 25 && days <= 29) {
			a = 25;
		} else if (days == 30) {
			a = 30;
		}

		return a;
	}
}
