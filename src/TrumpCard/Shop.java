package TrumpCard;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;

public class Shop {
    private GameState state;

    private VBox shopBox;
    private GridPane itemsPane;
    private Label scoreLbl;
    private Label itemDescriptionLbl;
    private Label errorLabel;
    private Button buyButton;

    private Item[] clothingItems;
    private Item[] gadgetItems;
    private Item[] foodItems;

    private ColorAdjust blackAndWhite;

    private ArrayList<HBox> itemControls;
    private Item currentSelection;

    private Item[] currentCategory;

    Shop(GameState state) {
        clothingItems = new Item[]{
                new Clothing("Iron armour", UIUtils.loadImage("file:images/item_ironarmour.png"), 25, CharacterName.IronMan,
                        "Iron Man's red armour will turn your character icon on the screen red.", "red"),
                new Clothing("Blue scepter", UIUtils.loadImage("file:images/item_bluescepter.png"), 25, CharacterName.IronMan,
                        "This shiny weapon will turn your character icon blue.", "blue"),
                new Clothing("Black cloak", UIUtils.loadImage("file:images/item_blackcloak.png"), 25, CharacterName.Batman,
                        "This special Black cloak will disguise you in a unique black character icon.", "black"),
                new Clothing("Golden boots", UIUtils.loadImage("file:images/item_goldenboots.png"), 25, CharacterName.Batman,
                        "These golden boots will be everyone's favourite fashion accessory, turning your character's" +
                                "icon gold.", "gold"),
                new Clothing("Red cloak", UIUtils.loadImage("file:images/item_redcloak.png"), 25, CharacterName.Spiderman,
                        "This cloak will turn your character's icon red.", "red"),
                new Clothing("Viper's tears", UIUtils.loadImage("file:images/item_bluecloak.png"), 25, CharacterName.Spiderman,
                        "Tears, just like water, are blue. Viper's tears will dye your cloak blue.", "blue")
        };

        gadgetItems = new Item[]{
                new Gadget("Jarvis", UIUtils.loadImage("file:images/item_jarvis.png"), 50, CharacterName.IronMan,
                        "Jarvis will assist Iron Man and gain you 5 extra durability points.", 0, 0, 5),
                new Gadget("Internet Access", UIUtils.loadImage("file:images/item_internetaccess.png"), 20, CharacterName.Ultron,
                        "Getting the ability to access the internet will give Ultron a boost of " +
                                "2 points in intelligence which will allow you to commit/fight crimes using less energy.", 0, 2, 0),
                new Gadget("Batmobile", UIUtils.loadImage("file:images/item_batmobile.png"), 30, CharacterName.Batman,
                        "The Batmobile will allow Batman to move faster by giving it 3 extra strength points.", 3, 0, 0),
                new Gadget("Catwoman's whip", UIUtils.loadImage("file:images/item_whip.png"), 10, CharacterName.Catwoman,
                        "The whip will make Catwoman stronger by one point.", 1, 0, 0),
                new Gadget("Spider suit", UIUtils.loadImage("file:images/item_spidersuit.png"), 5, CharacterName.Spiderman,
                        "The spider suit will increase your character's durability by 1 point.", 0, 0, 1),
                new Gadget("Goblin glider", UIUtils.loadImage("file:images/item_goblinglider.png"), 40, CharacterName.GreenGoblin,
                        "The Goblin Glider will allow your character to glide faster by adding 4 points to your strength.", 4, 0, 0)
        };

        foodItems = new Item[]{
                new Food("Coffee", UIUtils.loadImage("file:images/item_coffee.png"), 30, CharacterName.IronMan,
                        "Your character's energy will be recharged by 30% thanks to this coffee.", 0, 0, 0, 30, 0),
                new Food("Electricity", UIUtils.loadImage("file:images/item_electricity.png"), 20, CharacterName.Ultron,
                        "The electricity will give your character a well needed kick, it will decrease your action" +
                                " points turning you more evil.", 0, 0, 0, 0, -5),
                new Food("Energy Drink", UIUtils.loadImage("file:images/item_energydrink.png"), 60, CharacterName.Batman,
                        "This energy drink will provide a refreshing energy boost for your character.", 0, 0, 0, 45, 0),
                new Food("Catnip", UIUtils.loadImage("file:images/item_catnip.png"), 25, CharacterName.Catwoman,
                        "This catnip will make your character go nuts, especially if your character is Catwoman. It" +
                                " will give you an additional 3 durability, making your character use less energy.", 0, 0, 3, 0, 0),
                new Food("Kiss from Mary Jane", UIUtils.loadImage("file:images/item_kissmaryjane.png"), 5, CharacterName.Spiderman,
                        "This kiss will make you stronger, but at what cost.", 1, -3, 0, 0, 0),
                new Food("Goblin Serum", UIUtils.loadImage("file:images/item_serum.png"), 30, CharacterName.GreenGoblin,
                        "This goblin serum will increase your durability.", 0, 0, 4, 0, 0)
        };

        this.state = state;

        blackAndWhite = new ColorAdjust();
        blackAndWhite.setSaturation(-1);

        itemControls = new ArrayList<HBox>();
    }

    private void showItemDesc(Item item)
    {
        String text = item.getName() + "\n";
        text += "Price: " + item.getPrice() + " points\n";
        text += item.getDescription();
        itemDescriptionLbl.setText(text);

        // Reset any old errors.
        errorLabel.setText("");
        if (!buyButton.getStyleClass().contains("disabledBtn")) {
            buyButton.getStyleClass().add("disabledBtn");
        }
        // Check if this character can buy this item.
        if (!item.getEligibleCharacter().isRelatedTo(state.getCharacter().getName()))
        {
            // Not eligible to buy so set error label to notify user.
            errorLabel.setText(state.getCharacter().getFriendlyName() + " cannot buy this item.");
        }
        // Check if item has already been bought.
        else if (state.getCharacter().hasItem(item))
        {
            errorLabel.setText("This item is already in your inventory.");
        }
        // Check if the player has enough funds to buy this item.
        else if (item.getPrice() > state.getCharacter().getScore())
        {
            errorLabel.setText("You need more points to buy this item.");
        }
        else
        {
            buyButton.getStyleClass().remove("disabledBtn");
        }
    }

    private void onItemMouseEnter(MouseEvent event, Item item)
    {
        showItemDesc(item);
    }

    private void resetItemDesc() {
        if (currentSelection != null)
        {
            showItemDesc(currentSelection);
        }
        else {
            itemDescriptionLbl.setText("");
            errorLabel.setText("");
        }
    }

    private void onItemMouseLeave(MouseEvent event, Item item)
    {
        resetItemDesc();
    }

    private void onItemMouseClicked(MouseEvent event, Item item, HBox itemPane)
    {
        currentSelection = item;
        for (HBox itemControl : itemControls)
        {
            if (itemControl == itemPane)
            {
                itemControl.getStyleClass().add("shopCategorySelected");
            }
            else
            {
                itemControl.getStyleClass().remove("shopCategorySelected");
            }
        }
    }

    private void onBackBtnClicked(MouseEvent event, Pane pausePane)
    {
        pausePane.getStyleClass().remove("shopPane");
        pausePane.getStyleClass().add("pausePane");
        pausePane.setVisible(false);
        state.resume();
    }

    private void onBuyBtnClicked(MouseEvent event, GridPane itemsPane)
    {
        // Ensure the selected item can be bought.
        if (buyButton.getStyleClass().contains("disabledBtn") || currentSelection == null)
        {
            return;
        }

        // Take away price from player's Score.
        state.getCharacter().setScore(state.getCharacter().getScore() - currentSelection.getPrice());

        state.getCharacter().addItem(currentSelection);

        // Refresh items.
        addItemButtons(itemsPane, currentCategory);

        // Reset button.
        buyButton.getStyleClass().add("disabledBtn");
    }

    private HBox createCharacterHBox(CharacterName[] names) {
        HBox result = new HBox(5);

        for (CharacterName name : names)
        {
            Image img = name.loadImage();
            ImageView imgView = new ImageView(img);
            imgView.setPreserveRatio(true);
            imgView.setFitHeight(64);
            if (!name.isRelatedTo(state.getCharacter().getName()))
            {
                // Make the character images black and white since the user cannot buy their items.
                imgView.setEffect(blackAndWhite);
            }
            result.getChildren().add(imgView);
        }
        return result;
    }

    private void addItemButtons(GridPane itemsPane, Item[] items)
    {
        currentCategory = items;
        // This method will be used when switching categories so children need to be cleared first.
        itemsPane.getChildren().clear();
        // Clear selection.
        itemControls.clear();
        currentSelection = null;
        resetItemDesc();
        // Add items to GridPane.
        for (int i = 0; i < 6; i++) {
            Item item = items[i];

            // Create a pane to hold the ImageView (so that we can get a border)
            HBox borderPane = new HBox();
            borderPane.setAlignment(Pos.CENTER);
            borderPane.setPrefWidth(132);
            borderPane.setPrefHeight(132);
            borderPane.getStyleClass().add("shopItem");
            // Check if item is already owned by character.
            if (state.getCharacter().hasItem(item))
            {
                borderPane.getStyleClass().add("boughtItem");
            }

            ImageView item1 = new ImageView(items[i].getImage());
            item1.setPreserveRatio(true);
            item1.setFitWidth(128);
            item1.setOnMouseEntered(event -> onItemMouseEnter(event, item));
            item1.setOnMouseExited(event -> onItemMouseLeave(event, item));
            item1.setOnMouseClicked(event -> onItemMouseClicked(event, item, borderPane));

            // Check if item applies to the current character.
            if (!item.getEligibleCharacter().isRelatedTo(state.getCharacter().getName()))
            {
                // Make the item black and white.
                item1.setEffect(blackAndWhite);
            }
            borderPane.getChildren().add(item1);

            itemsPane.add(borderPane, i % 2 == 0 ? 1 : 2, (int) Math.floor(i / 2));
            itemControls.add(borderPane);
        }

        // Add character images to show which items correspond to which characters.
        HBox ironManBox = createCharacterHBox(
                new CharacterName[]{CharacterName.IronMan, CharacterName.TonyStark, CharacterName.Ultron});
        itemsPane.add(ironManBox, 0, 0);
        HBox batmanBox = createCharacterHBox(
                new CharacterName[]{CharacterName.Batman, CharacterName.BruceWayne, CharacterName.Catwoman});
        itemsPane.add(batmanBox, 0, 1);
        HBox spidermanBox = createCharacterHBox(
                new CharacterName[]{CharacterName.Spiderman, CharacterName.PeterParker, CharacterName.GreenGoblin});
        itemsPane.add(spidermanBox, 0, 2);

        // Add category buttons to the right.
        Button clothingBtn = new Button("Clothing");
        clothingBtn.getStyleClass().add("shopCategoryBtn");
        if (items[0] instanceof Clothing)
        {
            clothingBtn.getStyleClass().add("shopCategorySelected");
        }
        clothingBtn.setOnMouseClicked(event -> addItemButtons(itemsPane, clothingItems));
        itemsPane.add(clothingBtn, 3, 0);

        Button gadgetsBtn = new Button("Gadgets");
        gadgetsBtn.getStyleClass().add("shopCategoryBtn");
        if (items[0] instanceof Gadget)
        {
            gadgetsBtn.getStyleClass().add("shopCategorySelected");
        }
        gadgetsBtn.setOnMouseClicked(event -> addItemButtons(itemsPane, gadgetItems));
        itemsPane.add(gadgetsBtn, 3, 1);

        Button foodBtn = new Button("Food");
        foodBtn.getStyleClass().add("shopCategoryBtn");
        if (items[0] instanceof Food)
        {
            foodBtn.getStyleClass().add("shopCategorySelected");
        }
        foodBtn.setOnMouseClicked(event -> addItemButtons(itemsPane, foodItems));
        itemsPane.add(foodBtn, 3, 2);

        // Tell the user how much points they have.
        scoreLbl.setText("You have " + state.getCharacter().getScore() + " points to spend.");
    }

    public void create(Pane pausePane)
    {
        // TODO: For testing
        state.getCharacter().setScore(25);

        // Controls associated with the shop.
        shopBox = new VBox(5);
        shopBox.setLayoutX(300);
        shopBox.setAlignment(Pos.CENTER);
        pausePane.getChildren().add(shopBox);
        shopBox.setVisible(false);

        Label shopTextLbl = new Label("Shop");
        shopTextLbl.setTextAlignment(TextAlignment.CENTER);
        shopTextLbl.setFont(Font.font("HAGANE", 46));
        shopBox.getChildren().add(shopTextLbl);

        itemsPane = new GridPane();
        itemsPane.setAlignment(Pos.CENTER);
        itemsPane.setHgap(10);
        itemsPane.setVgap(10);
        shopBox.getChildren().add(itemsPane);

        // This label will display the amount of score left.
        scoreLbl = new Label();
        scoreLbl.setFont(Font.font("Courier New", 16));
        scoreLbl.setTextAlignment(TextAlignment.CENTER);
        shopBox.getChildren().add(scoreLbl);

        // This label will display a description of the item selected.
        itemDescriptionLbl = new Label();
        itemDescriptionLbl.setPrefWidth(600);
        itemDescriptionLbl.setMinHeight(100);
        itemDescriptionLbl.setWrapText(true);
        itemDescriptionLbl.setFont(Font.font("Courier New", 15));
        shopBox.getChildren().add(itemDescriptionLbl);

        // This label will display reasons why the user can't buy an item.
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error");
        errorLabel.setFont(Font.font("Courier New", 15));
        shopBox.getChildren().add(errorLabel);

        HBox bottomButtons = new HBox(5);
        bottomButtons.setAlignment(Pos.CENTER);
        shopBox.getChildren().add(bottomButtons);

        Button backButton = new Button("Back");
        backButton.setOnMouseClicked(event -> onBackBtnClicked(event, pausePane));
        backButton.getStyleClass().add("buyBtn");
        bottomButtons.getChildren().add(backButton);

        buyButton = new Button("Buy");
        buyButton.getStyleClass().addAll("buyBtn", "disabledBtn");
        buyButton.setOnMouseClicked(event -> onBuyBtnClicked(event, itemsPane));
        bottomButtons.getChildren().add(buyButton);

    }

    public VBox getShopBox() {
        return shopBox;
    }

    public void showShop(GameScreen screen, Pane pausePane)
    {
        pausePane.getStyleClass().add("shopPane");
        pausePane.getStyleClass().remove("pausePane");
        screen.showPausePane();
        shopBox.setVisible(true);

        addItemButtons(itemsPane, gadgetItems);
    }

}
