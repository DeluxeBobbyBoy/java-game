import java.util.Arrays;
import java.util.Scanner;
public class Game {
    public static final String LINE = "--------------------------";
    public static final String TURN_BREAK = "========================================================================";
    public static final int CURRENT_LEVEL = 0;
    public static final int NEXT_LEVEL_REQ = 1;
    public static final int LEVEL_PROGRESS = 2;
    public static final int LEVEL_EARNED = 3;
    public static void main(String[] args) {
        Scanner ui = new Scanner(System.in);
        boolean exit = false;
        boolean gameEnd = false;
        boolean levelUp = false;
//        int[] Inventory = {3,4,2,2,2,2,2,2,2,2};
        int[][] shop = new int[7][2];
//        int[][] shop = {{1,50},{1,50},{2,100},{3,50},{4,75}};
        int[] Inventory = new int[10];
        int[] fillIndex = new int[5];
        int step = 0;
        while (fillIndex[4] < 1) {
            int number = (int)(4*Math.random()+1);
            boolean verify = false;
            while (!verify) {
                if (fillIndex[step] > 1 && !(checkIfSame(fillIndex, number))) {
                    verify = true;
                } else if (!checkIfSame(fillIndex, number)) {
                    fillIndex[step] = number;
                } else {
                    number = (int)(10*Math.random()+1);
                }
            }
            step++;
        }
        Arrays.sort(fillIndex);
        for (int i = 0; i < Inventory.length; i++) {
            for (int j = 0; j < fillIndex.length; j++) {
                if (i == fillIndex[j]) {
                    Inventory[i] = (int)(4*Math.random()+1);
                }
            }
        }
        int bossType = 0;
        int bossAtk = 0;
        int bossHealth = 500;
        int bossMaxHealth = 500;
        int userHealth = 500;
        int maxHealth = 500;
        int missingHealth = 0;
        int playerDamageTaken = 0;
        int bossDamageTaken = 0;
        int[] level = {1,500,0,0};
        int coins = 350;
        int coinsEarned = 0;
        int event = 0;

        printNameLine("epic gamer game");

        while (!exit && userHealth > 0) {
            for (int i = 0; i < shop.length; i++) {
                int item = (int)(4*Math.random()+1);
                shop[i][0] = item;
                shop[i][1] = assignPrice(item, bossType);
            }
            while (event == 0) {
                System.out.println("\n" + TURN_BREAK);
                printInventory(Inventory, level);
                player(userHealth, maxHealth, coins, level);
                printShop(shop, level);
                System.out.println(LINE);

                boolean validInput = false;
                while (!validInput) {
                    System.out.print("\nwhat would you like to buy? (Type 0 to leave) ");
                    int purchaseIndex = ui.nextInt()-1;
                    if (purchaseIndex == -1) {
                        validInput = true;
                        event = 1;
                    }
                    if (purchaseIndex > shop.length || purchaseIndex < -1 || !validInput && !(coins >= shop[purchaseIndex][1] && shop[purchaseIndex][0] >= 1)) {
                        System.out.println("Invalid purchase!");
                    }
                    else if (!validInput){
                        if (checkInventoryFull(Inventory) == -1) {
                            System.out.print("no space, would you like to override an item? ");
                            boolean choice = ui.nextBoolean();
                            if (choice) {
                                System.out.print("Which slot would you like to place this item? ");
                                int overrideIndex = ui.nextInt()-1;

                                Inventory[overrideIndex] = shop[purchaseIndex][0];
                                coins = coins - shop[purchaseIndex][1];
                                shop[purchaseIndex][0] = 0;
                                shop[purchaseIndex][1] = 0;

                                validInput = true;
                            }
                        }
                        else {
                            Inventory[checkInventoryFull(Inventory)] = shop[purchaseIndex][0];
                            coins = coins - shop[purchaseIndex][1];
                            shop[purchaseIndex][0] = 0;
                            shop[purchaseIndex][1] = 0;

                            validInput = true;
                        }
                    }
                }
            }
            while (event == 1 && userHealth > 0) {
                System.out.println(TURN_BREAK);

                boolean validInput = false;

                maxHealth = updateMaxHealth(maxHealth, level, testLevelUpdate(level));

                boss(bossType, bossHealth, bossMaxHealth);
                player(userHealth, maxHealth, coins, level);

                printInventory(Inventory, level);

                while (!validInput) {
                    System.out.print("\nUse item (0 for fist): ");
                    int inventoryIndex = ui.nextInt()-1;

                    if (inventoryIndex < Inventory.length && inventoryIndex >= -1) {
                        validInput = true;
                    } else {
                        System.out.println("\nPlease enter a valid input.");
                    }
                    if (validInput) {
                        bossAtk = (int)(25 * Math.random() + 3*bossType + 50);

                        if (inventoryIndex != -1) {
                            switch (Inventory[inventoryIndex]) {
                                case 0, 1, 2:
                                    bossDamageTaken = bossHealth - damage(bossHealth, Inventory, inventoryIndex, level);
                                    bossHealth = damage(bossHealth, Inventory, inventoryIndex, level);
                                    userHealth = damageUser(userHealth, bossAtk);
                                    playerDamageTaken = userHealth - damageUser(userHealth, bossAtk);

                                    level[LEVEL_EARNED] = bossDamageTaken;
                                    break;
                                case 3, 4:
                                    if (healCalc(level, Inventory[inventoryIndex])+userHealth > maxHealth) {
                                        level[LEVEL_EARNED] = (healCalc(level, Inventory[inventoryIndex]) + userHealth) - maxHealth;
                                    } else {
                                        level[LEVEL_EARNED] = healCalc(level, Inventory[inventoryIndex]);
                                    }

                                    bossDamageTaken = 0;
                                    userHealth = healUser(userHealth, maxHealth, Inventory, inventoryIndex, level);
                                    break;
                                default:
                                    System.out.println("error");
                            }

                            useItem(Inventory, inventoryIndex);
                            Inventory[inventoryIndex] = 0;
                        }
                        else {
                            bossDamageTaken = bossHealth - damage(bossHealth, Inventory, inventoryIndex, level);
                            bossHealth = damage(bossHealth, Inventory, inventoryIndex, level);
                            userHealth = damageUser(userHealth, bossAtk);
                            playerDamageTaken = userHealth - damageUser(userHealth, bossAtk);

                            level[LEVEL_EARNED] = bossDamageTaken;
                        }
                        levelUp = announceLevelUp(level,testLevelUpdate(level));
                        maxHealth = updateMaxHealth(maxHealth, level, testLevelUpdate(level));
                        updateLevel(level);
                    }
                }
                if (bossHealth <= 0) {
                    event = 2;
                }

                System.out.println(TURN_BREAK);
                System.out.println("\nYou took " + playerDamageTaken + " damage and you dealt " + bossDamageTaken + " damage. You earned " + level[LEVEL_EARNED] + " experience.");

                if (levelUp) {
                    System.out.println("You leveled up to level " + level[CURRENT_LEVEL] + "!");
                    levelUp = false;
                }
                System.out.println();

                level[LEVEL_EARNED] = 0;
                playerDamageTaken = 0;
            }
            boolean verify = false;
            if (userHealth > 0) {

                coinsEarned = + 500+(int)(bossType*100*Math.random());
                coins = coins + coinsEarned;

                level[LEVEL_EARNED] = 500+(500*bossType);
                levelUp = announceLevelUp(level,testLevelUpdate(level));
                maxHealth = updateMaxHealth(maxHealth, level, testLevelUpdate(level));
                updateLevel(level);

                System.out.println("You defeated the boss! You earned " + coinsEarned + " coins, " + level[LEVEL_EARNED] + " experience, and you found an item.\n");
                coinsEarned = 0;
                level[LEVEL_EARNED] = 0;

                if (levelUp) {
                    System.out.println("You leveled up to level " + level[CURRENT_LEVEL] + "!");
                    levelUp = false;
                }
                System.out.println();

                player(userHealth, maxHealth, coins, level);
                printInventory(Inventory, level);

                System.out.print("Would you like to pick up Damage II (dmg: " + damageCalc(level, 2) + ") (true or false)? ");
                verify = ui.nextBoolean();
            }
            if (verify) {
                while (event == 2) {
                    if (checkInventoryFull(Inventory) == -1) {
                        System.out.print("no space, would you like to override an item? ");
                        int choice = ui.nextInt()-1;
                        boolean verify2 = false;
                        if (choice == 1) {
                            verify2 = true;
                            event = 0;
                        }
                        else if (choice == 2) {
                            verify2 = false;
                            event = 0;
                        }
                        if (verify2) {
                            System.out.print("Which slot would you like to place the item? ");
                            int overrideIndex = ui.nextInt()-1;
                            Inventory[overrideIndex] = 2;
                        }
                    } else {
                        Inventory[checkInventoryFull(Inventory)] = 2;
                    }
                    System.out.println("You have picked up an item!");
                    printInventory(Inventory, level);
                    event = 0;
                }
            }
            else {
                event = 0;
            }
            bossMaxHealth = bossMaxHealth+100;
            bossHealth = bossMaxHealth;
            bossType++;
        }
        System.out.println("game over!");
    }
    public static void printInventory(int[] Inventory, int[] level) {
//        System.out.println();
        printNameLine("INVENTORY");
        for (int i = 0; i < Inventory.length; i++) {
            System.out.print(i+1 + ". ");
            switch (Inventory[i]) {
                case 0:
                    System.out.println("Empty");
                    break;
                case 1:
                    System.out.println("Damage I (dmg: " + damageCalc(level, 1) + ")");
                    break;
                case 2:
                    System.out.println("Damage II (dmg: " + damageCalc(level, 2) + ")");
                    break;
                case 3:
                    System.out.println("Healing I (hp: " + healCalc(level, 3) + ")");
                    break;
                case 4:
                    System.out.println("Healing II (hp: " + healCalc(level, 4) + ")");
                    break;
                default:
                    System.out.println("unknown item");
            }
        }
        System.out.println(LINE);
    }
    public static void useItem(int[] Inventory, int inventoryIndex) {
        switch (Inventory[inventoryIndex]) {
            case 0:
                System.out.println("Fist used");
                break;
            case 1:
                System.out.println("Damage I used");
                break;
            case 2:
                System.out.println("Damage II used");
                break;
            case 3:
                System.out.println("Healing I used");
                break;
            case 4:
                System.out.println("Healing II used");
                break;
            default:
                System.out.println("invalid choice");
        }
        System.out.println();
    }
    public static void boss(int bossType, int bossHealth, int bossMaxHealth) {
        printNameLine("BOSS STATS");
        System.out.println("Boss " + (bossType+1));
        System.out.println("Health: " + bossHealth + "/" + bossMaxHealth);
        System.out.println(LINE);
    }
    public static void player(int userHealth, int maxHealth, int coins, int[] level) {
        printNameLine("YOUR STATS");
        System.out.println("Health: " + userHealth + "/" + maxHealth);
        System.out.println("Level " + level[CURRENT_LEVEL] + ": " + level[LEVEL_PROGRESS] + "/" + level[NEXT_LEVEL_REQ]);
        System.out.println("Coins: " + coins);
        System.out.println(LINE);
    }
    public static int damage(int health, int[] Inventory, int inventoryIndex, int[] level) {
        if (inventoryIndex != -1) {
            switch (Inventory[inventoryIndex]) {
                case 1:
                    health = health - damageCalc(level, 1);
                    break;
                case 2:
                    health = health - damageCalc(level, 2);
                    break;
                default:
                    health = health - damageCalc(level, 0);
            }
        }
        else {
            health = health - damageCalc(level, 0);
        }
        return health;
    }
    public static int damageCalc(int[] level, int index) {
        int damage = 0;
        switch (index) {
            case 1:
                damage = 100;
                break;
            case 2:
                damage = 175;
                break;
            default:
                damage = 20;
        }
        damage = (int)(damage+((damage*0.25)*(level[CURRENT_LEVEL]-1)));
        return damage;
    }
    public static int damageUser(int health, int attack) {
        health = health-attack;
        return health;
    }
    public static int checkInventoryFull(int[] Inventory) {
        for (int i = 0; i < Inventory.length; i++) {
            if (Inventory[i] == 0) {
                return i;
            }
        }
        return -1;
    }
    public static void printShop(int[][] shop, int[] level) {
        printNameLine("SHOP");
        for (int i = 0; i < shop.length; i++) {
            System.out.print(i+1 + ". ");
            switch (shop[i][0]) {
                case 0:
                    System.out.print("Empty");
                    break;
                case 1:
                    System.out.print("Damage I (dmg: " + damageCalc(level, 1) + ")");
                    break;
                case 2:
                    System.out.print("Damage II (dmg: " + damageCalc(level, 2) + ")");
                    break;
                case 3:
                    System.out.print("Healing I (hp: " + healCalc(level, 3) + ")");
                    break;
                case 4:
                    System.out.print("Healing II (hp: " + + healCalc(level, 4) + ")");
                    break;
                default:
                    System.out.print("error");
            }
            if (shop[i][0] > 0) {
                System.out.print(" | cost: " + shop[i][1] + " coins.");
            }
            System.out.println();
        }
    }
    public static int healUser(int userHealth, int maxHealth, int[] Inventory, int inventoryIndex, int[] level) {
        switch(Inventory[inventoryIndex]) {
            case 3:
                userHealth = userHealth+healCalc(level, 3);
                break;
            case 4:
                userHealth = userHealth+healCalc(level, 4);
                break;
        }
        if (userHealth > maxHealth) {
            userHealth = maxHealth;
        }
        return userHealth;
    }
    public static int healCalc(int[] level, int index) {
        int heal = 0;
        switch (index) {
            case 3:
                heal = 50;
                break;
            case 4:
                heal = 125;
        }
        heal = (int)(heal+((heal*0.25)*(level[CURRENT_LEVEL]-1)));
        return heal;
    }
    public static int assignPrice(int item, int bossType) {
        int price = 0;
        switch (item) {
            case 1, 3:
                price = 50;
                break;
            case 2:
                price = 125;
                break;
            case 4:
                price = 175;
                break;
        }
        price = price + (25*bossType);
        return price;
    }
    public static boolean checkIfSame(int[] array, int number) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == number) {
                return true;
            }
        }
        return false;
    }
    public static void printNameLine(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ' ') {
                name = replaceCharAt(i, name, "-");
            }
        }
        for (int i = 0; i < LINE.length()-name.length(); i++) {
            if (i == (LINE.length()-name.length())/2) {
                System.out.print(name);
            }
            else {
                System.out.print("-");
            }
        }
        if ((LINE.length()/2)+name.length() < 26) {
            System.out.print("-");
        }
        System.out.println();
    }
    public static String replaceCharAt(int index, String name, String character) {
        String substring1 = "";
        String substring2 = "";
        for (int i = 0; i < name.length(); i++) {
            if (i == index) {
                substring1 = name.substring(0,i);
                substring2 = name.substring(i+1);
                substring1 = substring1 + character;
                name = substring1 + substring2;
            }
        }
        return name;
    }
    public static void updateLevel(int[] level) {
        level[LEVEL_PROGRESS] = level[LEVEL_PROGRESS] + level[LEVEL_EARNED];
        if (level[LEVEL_PROGRESS] >= level[NEXT_LEVEL_REQ]) {
            level[CURRENT_LEVEL] = level[CURRENT_LEVEL] + 1;
            level[LEVEL_PROGRESS] = (level[NEXT_LEVEL_REQ] - level[LEVEL_PROGRESS])*-1;
            level[NEXT_LEVEL_REQ] = level[NEXT_LEVEL_REQ]+(level[NEXT_LEVEL_REQ]/10)+250;
        }
    }
    public static int updateMaxHealth(int maxHealth, int[] level, int[] levelCheck) {
        if (levelCheck[CURRENT_LEVEL] > level[CURRENT_LEVEL]) {
            maxHealth = maxHealth + (((maxHealth/10)*(levelCheck[CURRENT_LEVEL]))/2);
        }
        return maxHealth;
    }
    public static boolean announceLevelUp(int[] level, int[] levelCheck) {
        return levelCheck[CURRENT_LEVEL] > level[CURRENT_LEVEL];
    }
    public static int[] testLevelUpdate(int[] level) {
        int[] newArray = new int[4];
        newArray[LEVEL_EARNED] = level[LEVEL_EARNED];
        newArray[LEVEL_PROGRESS] = level[LEVEL_PROGRESS] + level[LEVEL_EARNED];
        if (newArray[LEVEL_PROGRESS] >= level[NEXT_LEVEL_REQ]) {
            newArray[CURRENT_LEVEL] = level[CURRENT_LEVEL] + 1;
            newArray[LEVEL_PROGRESS] = (level[NEXT_LEVEL_REQ] - level[LEVEL_PROGRESS])*-1;
            newArray[NEXT_LEVEL_REQ] = level[NEXT_LEVEL_REQ]*level[CURRENT_LEVEL]+(level[NEXT_LEVEL_REQ]/10);
        }
        return newArray;
    }
}
