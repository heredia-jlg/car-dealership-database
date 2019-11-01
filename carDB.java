import java.util.Scanner;
import java.sql.*;

class carDB {

    public static Scanner input = new Scanner(System.in);
    public static Statement statement = null;
    public static Connection connection;


    public static void main(String[] args) {


        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://cs.neiu.edu:3306/cs315sum19_jlgonza2?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&user=jlgonza2&password=database" );
            statement = connection.createStatement();

            menu();

        }
        catch( SQLException sqle)
        {
            System.out.println("SQLException: " + sqle.getMessage() );
            System.out.println("SQLState: " + sqle.getSQLState() );
        }
        catch( Exception e )
        {
            System.out.print( e.getMessage() );
        }
        finally
        {
            try {
                statement.close();
                connection.close();
            }catch( Exception e )
            {
                System.out.print( e.getMessage() );
            }
        }

    }


    public static void menu() throws Exception
    {
        int option = -1;
        while( option <= 4 )
        {
            System.out.println("#### This is the Dealership's database ####");
            System.out.println("#### Choose one of the following options: ####");
            System.out.println();

            System.out.println("1 : Add new car");
            System.out.println("2 : Show Car Inventory");
            System.out.println("4 : Quit");

            option = input.nextInt();

            if( option == 1)
            {
                int id = add("Car");
            }
            else if( option == 2 )
            {
                showInventory("Car");

            }
            else if (option == 4)
            {
                System.out.println("Bye");
            }

        }

    }


    public static int add(String table) throws Exception //returns id
    {

        String columns = getColumns( table );
        String values = getValues( table );
        int rows = 0;

        String sql = "INSERT INTO " + table + "( " + columns + " ) VALUES( " + values + " )" ;

        rows = statement.executeUpdate( sql );

        System.out.println("This many rows were affected: " + rows);

        System.out.println();


        return getId(table, values);

    }

    public static int getId(String table, String values) throws Exception
    {
        int id = 0;
        String columns[] = getColumns(table).split(", ");
        String valuesArray[] = values.split(", ");

        String sql = "SELECT * FROM " + table + " WHERE  " +columns[0]+" =" +valuesArray[0]+" AND " +columns[1]+" =" +valuesArray[1];

      
        System.out.println();
        System.out.println();

        ResultSet rs =statement.executeQuery( sql );

        while( rs.next() )
        {
            id = rs.getInt(1);
        }


        return id;
    }


    public static String getColumns(String table)
    {
        String columns = "";

        if( table == "Car")
        {
            columns = "c_make, c_model, c_year, c_price";
        }
        else if( table == "Buyer")
        {
            columns = "b_firstName, b_lastName, b_address, b_email";
        }
        else if( table == "Seller")
        {
            columns = "s_firstName, s_lastName ";
        }
        else if( table == "Purchase")
        {
            columns = "p_id, bc_id, b_id, s_id";
        }

        return columns;
    }


    public static String getValues( String table)
    {
        String values = "";

        if( table == "Car")
        {
            System.out.println("Enter car's make: ");
            values = values + " '" + input.next() + "', ";

            input.nextLine();
            System.out.println("Enter car's model: ");
            values = values + "'" + input.nextLine() + "', ";

            System.out.println("Enter car's year: ");
            values = values + "'" + input.next() + "', ";

            System.out.println("Enter car's price: ");
            values = values + "'" +  Double.toString(input.nextDouble()) + "'";

        }
        else if( table == "Buyer")
        {

            System.out.println("Enter buyer's first name: ");
            values = values + " '" + input.next() + "', ";

            System.out.println("Enter buyer's last name: ");
            values = values + "'" + input.next() + "', ";

            input.nextLine();
            System.out.println("Enter buyer's address: ");
            values = values + "'" + input.nextLine() + "', ";

            System.out.println("Enter buyer's email: ");
            values = values + "'" +  input.nextLine() + "'";

        }
        else if( table == "Seller")
        {
            System.out.println("Enter seller's first name: ");
            values = values + " '" + input.next() + "', ";

            input.nextLine();
            System.out.println("Enter seller's last name: ");
            values = values + "'" + input.nextLine() + "' ";
        }

        return values;
    }


    public static void showInventory(String table) throws Exception
    {

        String columns = getColumns(table);
        String[] columnsArray = columns.split(", ");
        String sql = "SELECT " + "c_id," + columns + " FROM " + table;
        int car_id = 0;
        int index = 1;


        ResultSet rs =statement.executeQuery( sql );


        System.out.println("#### This is the inventory  ####");
        System.out.println();

        while( rs.next() )
        {
            car_id = rs.getInt( "c_id" );
            String make = rs.getString( columnsArray[0] );
            String model = rs.getString( columnsArray[1] );
            String year = rs.getString( columnsArray[2] );
            String price = rs.getString( columnsArray[3] );

            System.out.println( index + ":  Make: "+ make + "  Model : " + model + "  Year: " + year + "  Price: " + price);
            System.out.println();

            index++;
        }

        System.out.println("Is someone buying a car?");
        System.out.println(" 1 : Yes    2 : No");
        System.out.println();

        int option = input.nextInt();

        if( option == 1)
        {
            buyCar( rs, car_id );
        }
        else if (option == 2)
        {
            System.out.println("You chose to not buy a car");
        }
        else
        {
            System.out.println("That was not an option");
        }
    }


    public static void buyCar( ResultSet cars , int id) throws Exception
    {
        int carIndex = 0;
        String columns = getColumns("Car");
        String[] columnsArray = columns.split(", ");
        int rows;
        int buyerId = 0;
        int sellerId = 0;

        System.out.println("Enter the index of the car: ");
        System.out.println();

        carIndex = input.nextInt();

        cars.absolute( carIndex );

        String make = cars.getString( columnsArray[0] );
        String model = cars.getString( columnsArray[1] );
        String year = cars.getString( columnsArray[2] );
        String price = cars.getString( columnsArray[3] );

        System.out.println("This is the car you're buying:");
        System.out.println("ID: "+ carIndex +" Make: "+ make + "  Model : " + model + "  Year: " + year + "  Price: " + price);
        System.out.println();



        //Delete From Car
        String q = "Update Car set c_purchased = 1 WHERE c_id = " + carIndex;
        statement.executeUpdate(q);

        
        rows = statement.executeUpdate( q );

        System.out.println("This many rows were affected: " + rows);
        System.out.println();


         /*
        //get ids from buyer and seller
        String purchase = "INSERT INTO Purchase (bc_id, b_id, s_id) VALUES (" +id+ " , " + buyerId + ", " +sellerId+ ")";
      

        statement.executeUpdate(purchase);
        */



    }




}