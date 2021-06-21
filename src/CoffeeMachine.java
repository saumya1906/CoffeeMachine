
import java.util.*;
import java.lang.*;
// import java.io.*;
// import org.json.simple.*;
 
 import java.io.FileNotFoundException;
 import java.io.FileReader;
 import java.io.IOException;
 
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.json.simple.parser.JSONParser;
 import org.json.simple.parser.ParseException;


public class CoffeeMachine {
  // indicating value that an ingredient is running low.
  static final int INGREDIENT_THRESHOLD = 30;

  // Class for an Ingredient. 
  static public class Ingredient {
    private String name;
    private int ingredientRefillValue;
    private int totalQuantity;

    public Ingredient() {}
    public Ingredient(String name, int totalQuantity, int ingredientRefillValue) {
      this.name = name;
      this.totalQuantity = totalQuantity;
      this.ingredientRefillValue = ingredientRefillValue;
    }

    public String getName() {
      return this.name;
    }

    public int getQuantity() {
      return this.totalQuantity;
    }
    
    // Sets the total quantity of ingredient on being used up by a beverage.
    public void consumeQuantity(int consumedQuantity) {
      this.totalQuantity -= consumedQuantity;
    }

    // Refills the ingredient to it's maximum value.
    public void refillIngredient() {
      this.totalQuantity = this.ingredientRefillValue;
    }

    // Checks if the ingredient is running low.
    public boolean checkWarning() {
      return this.totalQuantity < INGREDIENT_THRESHOLD;
    }
  }

  // Custom Pair made for ingredients related to one specific beverage
  static public class CustomPair {
    private String name;
    private int beverageQuantity;

    public CustomPair() {}
    public CustomPair(String name, int beverageQuantity) {
      this.name = name;
      this.beverageQuantity = beverageQuantity;
    }

    public String getName() {
      return this.name;
    }

    public int getBeverageQuantity() {
      return this.beverageQuantity;
    }

  }

  // Class for a beverage.
  static public class Beverage {
    private String name;
    private ArrayList <CustomPair> ingredientList;

    public Beverage() {}
    public Beverage(String name) {
      this.name = name;
      this.ingredientList = new ArrayList <CustomPair> ();
    }
    
    // Adds an ingredient which is required to make this beverage.
    public void addIngredient(String name, int quantityUsed) {
      this.ingredientList.add(new CustomPair(name, quantityUsed));
    }

    public ArrayList <CustomPair> getIngredientList() {
      return this.ingredientList;
    }

    public String getName() {
      return this.name;
    }
  }

  // The actual functioning class which process everything.
  static public class MakeBeverage {
    private ArrayList <Beverage> beverages; // List of beverages to be made.
    private HashMap <String, Ingredient> totalIngredients; // List of total ingredients.

    public MakeBeverage() {
      this.totalIngredients = new HashMap <String, Ingredient> ();
      this.beverages = new ArrayList <Beverage> ();
    }

    // Adds beverage to the list.
    public void addBeverage(Beverage b) {
      this.beverages.add(b);
    }

    public void addIngredient(Ingredient i) {
      this.totalIngredients.put(i.getName(), i);
    }


    // The parameters returned when the machine checks if it's possible to make it or not.
    public class BeverageCheckReturn {
      private boolean result; // Result if it's possible to make the beverage or not.
      private String ingredientName; // Name of the missing ingredient. Empty if none.
      // If it's not possible to make the beverage due to missing ingredient in the whole 
      // stock. This is different than the case when an ingredients lacks in quantity.
      private boolean ingredientAvailability;


      public BeverageCheckReturn() {}
      public BeverageCheckReturn(boolean result, String ingredientName, boolean ingredientAvailability) {
        this.result = result;
        this.ingredientName = ingredientName;
        this.ingredientAvailability = ingredientAvailability;
      }

      public boolean getresult() {
        return this.result;
      }

      public String getIngredientName() {
        return this.ingredientName;
      }

      public boolean getIngredientAvailability() {
        return this.ingredientAvailability;
      }
    }

    public BeverageCheckReturn checkBeveragePossible(Beverage b) {
      // Ingredient list of beverage.
      ArrayList <CustomPair> beverageIngredientList = b.getIngredientList();
      boolean isPossible = true;
      String reasonIngredient = "";

      // for each ingredient of a beverage:
      for(CustomPair beverageIngredient: beverageIngredientList) {
        // Check if totalthe ingredient is even made in the stock
        if(!totalIngredients.containsKey(beverageIngredient.getName())) {
          return new BeverageCheckReturn(false, beverageIngredient.getName(), false);
        }

        Ingredient ingredient = totalIngredients.get(beverageIngredient.getName());
        // Ingredient quantity is less than required.
        if(beverageIngredient.getBeverageQuantity() > ingredient.getQuantity()) {
          reasonIngredient = beverageIngredient.getName();
          isPossible = false;
        }
      }
      // If it is possible to make the beverage
      if(isPossible) {
        // For each ingredient of a beverage.
        for(CustomPair beverageIngredient: beverageIngredientList) {
          Ingredient ingredient = totalIngredients.get(beverageIngredient.getName());
          // Subtract the remaining quantity of that ingredient.
          ingredient.consumeQuantity(beverageIngredient.getBeverageQuantity());

          // Check if the ingredient supply is running low. This will cause repeated warning as intended.
          if(ingredient.checkWarning()) {
            System.out.println("******* Ingredient " + ingredient.getName() + " is running low: " + ingredient.getQuantity() + " ************");
          }
        }
      }

      return new BeverageCheckReturn(isPossible, reasonIngredient, true);
    }

    public void startMakingBeverage() {
      for(int i = 0; i < beverages.size(); i++) {
        Beverage beverage = beverages.get(i);
        BeverageCheckReturn brp = checkBeveragePossible(beverage);
        // Processing the return and outputting accordingly.
        if(brp.getresult()) {
          System.out.println("Beverage: " + beverage.getName() + " is prepared");
        } else if(brp.getIngredientAvailability()) {
          System.out.println("Beverage: " + beverage.getName() + " cannot be prepared because " + brp.getIngredientName() + " is not sufficient.");
        } else {
          System.out.println("Beverage: " + beverage.getName() + " cannot be prepared because " + brp.getIngredientName() + " is not available.");
        }
      }
    }
  }

  public static void main(String[] args) {

    MakeBeverage mb = new MakeBeverage();
//    Ingredient i1 = new Ingredient("tea", 500, 10000);
//    Ingredient i2 = new Ingredient("coffee", 400, 10000);
//    mb.addIngredient(i1);
//    mb.addIngredient(i2);
//
//
//    Beverage b1 = new Beverage("popat");
//    b1.addIngredient("tea", 220);
//    b1.addIngredient("coffee", 230);
//
//    Beverage b2 = new Beverage("topap");
//    b2.addIngredient("tea", 276);
//    b2.addIngredient("coffee", 172);
//
//    mb.addBeverage(b1);
//    mb.addBeverage(b2);
//
//    mb.startMakingBeverage();


    
     JSONParser jsonParser = new JSONParser();
         // Parsing the json file and extracting the values and arranging them as class objects 
     // and sending to MakeBeverage class. 
         try (FileReader reader = new FileReader("src/tc5.json"))
         {
             Object o = jsonParser.parse(reader);
             JSONObject obj = (JSONObject) o;
             obj = (JSONObject) obj.get("machine");
             
             JSONObject beverages = (JSONObject) obj.get("beverages");
             
             for(Iterator iterator = beverages.keySet().iterator(); iterator.hasNext();) {
                  String key = (String) iterator.next();
                  Beverage b = new Beverage(key);
//                  System.out.println(key + " " + beverages.get(key));

                  JSONObject ingredientList = (JSONObject) beverages.get(key);
                  for(Iterator iterator1 = ingredientList.keySet().iterator(); iterator1.hasNext();) {
                    String key1 = (String) iterator1.next();
//                    System.out.println(key + " " + key1 + " " + ingredientList.get(key1));
                    b.addIngredient(key1, Integer.parseInt(ingredientList.get(key1).toString()));
                  }
                  
                  mb.addBeverage(b);
              }
             
             JSONObject ingredients = (JSONObject) obj.get("total_items_quantity");
             for(Iterator iterator = ingredients.keySet().iterator(); iterator.hasNext();) {
               String key = (String) iterator.next();
               Ingredient i = new Ingredient(key, Integer.parseInt(ingredients.get(key).toString()), 1000);
               mb.addIngredient(i);
             }
             
             
             mb.startMakingBeverage();
             
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (ParseException e) {
             e.printStackTrace();
         }
  }
}