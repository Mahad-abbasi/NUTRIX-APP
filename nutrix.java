import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Window;
import static jdk.nashorn.internal.objects.NativeJava.type;
import static org.omg.CORBA.AnySeqHelper.type;

public class nutrix extends Application {

    private Stage window;
    private Button signupBtn, loginBtn;

    public static void main(String[] args) {
        launch(args);
    }

    // ========================= MAIN APP PAGES =============================

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        // Splash Screen
        ImageView logo = new ImageView(new Image("resources/nutrix.png"));
        logo.setFitWidth(150);
        logo.setFitHeight(150);
        StackPane splashLayout = new StackPane(logo);
        splashLayout.setStyle("-fx-background-color: #0C3B2E;");
        Scene splashScene = new Scene(splashLayout, 1380, 700);

        window.setScene(splashScene);
        window.setTitle("Nutrix App");
        window.show();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> showMainMenuPage());
        pause.play();
    }

    private void showMainMenuPage() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0C3B2E;");

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome to");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 23));
        welcomeLabel.setTextFill(Color.LIGHTGRAY);

        Label appNameLabel = new Label("Nutrix");
        appNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        appNameLabel.setTextFill(Color.web("#FFBA00"));
        titleBox.getChildren().addAll(welcomeLabel, appNameLabel);

        HBox imageBox = new HBox(10);
        imageBox.setAlignment(Pos.CENTER);
        String[] pics = {"resources/slide2.png", "resources/slide3.png", "resources/slide4.png"};
        for (String pic : pics) {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(pic)));
            iv.setFitWidth(250);
            iv.setFitHeight(250);
            iv.setPreserveRatio(false);
            addHoverEffect(iv);
            imageBox.getChildren().add(iv);
        }

        Label messageLabel = new Label("Ready for some wins?\nStart tracking, it's easy!");
        messageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 18));
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        signupBtn = new Button("Sign Up For Free");
        loginBtn = new Button("Log In");

        highlightButton(signupBtn);
        unhighlightButton(loginBtn);

        signupBtn.setOnAction(e -> {
            highlightButton(signupBtn);
            unhighlightButton(loginBtn);
            showSignUpPage();
        });

        loginBtn.setOnAction(e -> {
            highlightButton(loginBtn);
            unhighlightButton(signupBtn);
            showLoginPage();
        });

        VBox centerBox = new VBox(30, titleBox, imageBox, messageLabel, signupBtn, loginBtn);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        window.setScene(new Scene(root, 1380, 700));
    }

    private void showSignUpPage() {
        Label heading = new Label("Sign Up");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#FFBA00"));

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);
        messageLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        TextField nameField = createInputBox();
        TextField emailField = createInputBox();
        PasswordField passwordField = createPasswordBox();

        VBox formBox = new VBox(15,
                new Label(""), // Empty label for spacing
                new Label(""), // Empty label for spacing
                createWhiteLabel("Name"), nameField,
                createWhiteLabel("Email"), emailField,
                createWhiteLabel("Password"), passwordField,
                messageLabel);
        formBox.setAlignment(Pos.CENTER);

        Button submit = new Button("Submit");
        highlightButton(submit);
        submit.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passwordField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                messageLabel.setText("Invalid email address.");
                return;
            }

            File file = new File("users.txt");
            boolean exists = false;

            try {
                if (!file.exists()) file.createNewFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[1].equalsIgnoreCase(email)) {
                        exists = true;
                        break;
                    }
                }
                reader.close();
            } catch (IOException ex) {
                messageLabel.setText("Error reading file.");
                return;
            }

            if (exists) {
                messageLabel.setText("Email already registered.");
                return;
            }

            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(name + "," + email + "," + pass + "\n");
                messageLabel.setTextFill(Color.LIGHTGREEN);
                messageLabel.setText("Sign Up Successful!");
                nameField.clear();
                emailField.clear();
                passwordField.clear();
                showDashboard(); // Navigate to dashboard on successful sign-up
            } catch (IOException ex) {
                messageLabel.setText("Error saving data.");
            }
        });

        VBox content = new VBox(20, heading, formBox, submit);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(400);
        content.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-padding: 20; -fx-background-radius: 10;");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #0C3B2E;");
        layout.setTop(createBackArrow());
        layout.setCenter(content);

        window.setScene(new Scene(layout, 1380, 700));
    }

    private void showLoginPage() {
        Label heading = new Label("Login");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#FFBA00"));

        Label messageLabel = new Label("");
        messageLabel.setTextFill(Color.RED);

        TextField nameField = createInputBox();
        PasswordField passwordField = createPasswordBox();

        VBox formBox = new VBox(15,
                new Label(""), // Empty label for spacing
                new Label(""), // Empty label for spacing
                createWhiteLabel("Name"), nameField,
                createWhiteLabel("Password"), passwordField,
                messageLabel);
        formBox.setAlignment(Pos.CENTER);

        Button submit = new Button("Submit");
        highlightButton(submit);
        submit.setOnAction(e -> {
            String name = nameField.getText().trim();
            String pass = passwordField.getText().trim();

            if (name.isEmpty() || pass.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[0].equals(name) && parts[2].equals(pass)) {
                        found = true;
                        break;
                    }
                }
            } catch (IOException ex) {
                messageLabel.setText("Error reading file.");
                return;
            }

            if (found) {
                messageLabel.setTextFill(Color.LIGHTGREEN);
                messageLabel.setText("Login Successful!");
                nameField.clear();
                passwordField.clear();
                showDashboard(); // Navigate to dashboard on successful login
            } else {
                messageLabel.setText("Invalid credentials.");
            }
        });

        VBox content = new VBox(20, heading, formBox, submit);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(400);
        content.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-padding: 20; -fx-background-radius: 10;");

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #0C3B2E;");
        layout.setTop(createBackArrow());
        layout.setCenter(content);

        window.setScene(new Scene(layout, 1380, 700));
    }

    // =================== DASHBOARD & NAVIGATION BETWEEN PAGES ========================
private void showDashboard() {
    // === Side Menu ===
    VBox sideMenu = new VBox(30);
    sideMenu.setStyle("-fx-background-color:#0C3B2E; -fx-border-color: white; -fx-border-width: 0 2 0 0;");
    sideMenu.setPadding(new Insets(50, 20, 20, 20));
    sideMenu.setPrefWidth(250);
    sideMenu.setAlignment(Pos.TOP_CENTER);
    sideMenu.setVisible(false);
    sideMenu.setManaged(false); // Important to remove from layout when hidden

    Label dashboard = new Label("Dashboard");
    Label contact = new Label("Contact Us");
    Label about = new Label("About Us");

    for (Label label : new Label[]{dashboard, contact, about}) {
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", 20));
        label.setStyle("-fx-cursor: hand;");
    }

    sideMenu.getChildren().addAll(dashboard, contact, about);

    // === Menu Button === (Left side)
    Label menuButton = new Label("☰");
    menuButton.setFont(Font.font("Arial", 21));
    menuButton.setTextFill(Color.WHITE);
    menuButton.setPadding(new Insets(10));
    menuButton.setStyle("-fx-cursor: hand;");

    HBox menuBar = new HBox(menuButton);
    menuBar.setAlignment(Pos.TOP_LEFT);
    menuBar.setPadding(new Insets(20, 0, 0, 20));

    // === Heading (Second line) ===
    Label heading = new Label("Dashboard");
    heading.setFont(Font.font("Arial", 30));
    heading.setTextFill(Color.WHITE);
    HBox headingBox = new HBox(heading);
    headingBox.setAlignment(Pos.CENTER);
    headingBox.setPadding(new Insets(10));

    // === Feature Grid (centered) with buttons that are NON-FUNCTIONAL on click ===
    GridPane featureGrid = new GridPane();
    featureGrid.setHgap(20);
    featureGrid.setVgap(20);
    featureGrid.setAlignment(Pos.CENTER);
    featureGrid.setPadding(new Insets(10, 20, 20, 20));

    // Create buttons for features as Blocks WITHOUT click events (non-functional)
    Button workoutPlanBtn = createFeatureButton("🏋", "Workout Plan", "#1ABC9C");
    // No action handler on purpose - non-functional

    Button bmiCalcBtn = createFeatureButton("⚖", "BMI Calculator", "#F39C12");
    // No action handler on purpose - non-functional

    Button calorieTrackerBtn = createFeatureButton("🍱", "Calorie Tracker", "#E74C3C");
    // No action handler on purpose - non-functional

    Button healthyTipsBtn = createFeatureButton("💡", "Healthy Tips", "#8E44AD");
    // No action handler on purpose - non-functional

    Button fruitVeggieBtn = createFeatureButton("🍏", "Fruit & Veggie Benefits", "#2ECC71");
    // No action handler on purpose - non-functional

    Button dietPlanBtn = createFeatureButton("🥗", "Diet Plan", "#B46617");
    // No action handler on purpose - non-functional

    featureGrid.add(workoutPlanBtn, 0, 0);
    featureGrid.add(bmiCalcBtn, 1, 0);
    featureGrid.add(calorieTrackerBtn, 2, 0);
    featureGrid.add(healthyTipsBtn, 0, 1);
    featureGrid.add(fruitVeggieBtn, 1, 1);
    featureGrid.add(dietPlanBtn, 2, 1);

    // === Welcome Text ===
    Label welcome = new Label("Welcome to Nutrix");
    welcome.setFont(Font.font("Arial", 28));
    welcome.setTextFill(Color.WHITE);

    Label motivation = new Label("Ready for some wins? Start tracking, it's easy!");
    motivation.setFont(Font.font("Arial", 16));
    motivation.setTextFill(Color.web("#6D9773"));

    VBox welcomeBox = new VBox(10, welcome, motivation);
    welcomeBox.setAlignment(Pos.CENTER);
    welcomeBox.setPadding(new Insets(20));

    // === Image Grid (Bottom buttons, FUNCTIONAL as required) ===
    GridPane imageGrid = new GridPane();
    imageGrid.setHgap(20);
    imageGrid.setVgap(20);
    imageGrid.setAlignment(Pos.CENTER);
    imageGrid.setPadding(new Insets(20));

    // Build image blocks with functional clicks:
    VBox img1 = createImageBlock("resources/fruitandveggie.png", "FruitVeggieBenifits");
    img1.setOnMouseClicked(e ->openFruitVeggiePage());
    
    VBox img2 = createImageBlock("resources/calorie.png", "CalorieTracker");
    img2.setOnMouseClicked(e -> openCalorieTracker());
    
    VBox img3 = createImageBlock("resources/bmi.png", "BMICalculator");
    img3.setOnMouseClicked(e -> openBMICalculator());
    
    VBox img4 = createImageBlock("resources/workout.png", "Workout Plan");
    img4.setOnMouseClicked(e -> openWorkoutPlanPage());

    VBox img5 = createImageBlock("resources/healthytips.png", "Healthy Tips");
    img5.setOnMouseClicked(e -> openHealthyTipsPage());

    VBox img6 = createImageBlock("resources/dietplan.png", "Diet Plan");
    img6.setOnMouseClicked(e -> openDietPlanPage());

    imageGrid.add(img1, 0, 0);
    imageGrid.add(img2, 1, 0);
    imageGrid.add(img3, 2, 0);
    imageGrid.add(img4, 0, 1);
    imageGrid.add(img5, 1, 1);
    imageGrid.add(img6, 2, 1);

    // === Bottom Spacer ===
    Region bottomSpacer = new Region();
    bottomSpacer.setMinHeight(60);

    // === Content VBox ===
    VBox contentBox = new VBox(10, menuBar, headingBox, featureGrid, welcomeBox, imageGrid, bottomSpacer);
    contentBox.setStyle("-fx-background-color: #0C3B2E;");
    contentBox.setSpacing(20);

    // === ScrollPane ===
    ScrollPane scrollPane = new ScrollPane(contentBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: #0C3B2E;");
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    // === Overlay for Side Menu ===
    HBox overlay = new HBox();
    overlay.getChildren().addAll(sideMenu);
    overlay.setPickOnBounds(false);
    overlay.setMouseTransparent(true);

    StackPane root = new StackPane(scrollPane, overlay);

    // === Menu Button Action ===
    menuButton.setOnMouseClicked(e -> {
        boolean show = !sideMenu.isVisible();
        sideMenu.setVisible(show);
        sideMenu.setManaged(show);
        overlay.setMouseTransparent(!show);

        if (show) {
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        } else {
            overlay.setStyle(null);
        }
    });

    // === Click outside to close side menu ===
    root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
        if (sideMenu.isVisible() && !sideMenu.isHover() && e.getTarget() != menuButton) {
            sideMenu.setVisible(false);
            sideMenu.setManaged(false);
            overlay.setMouseTransparent(true);
            overlay.setStyle(null);
        }
    });

    // Side menu label clicks - open appropriate pages
    dashboard.setOnMouseClicked(e -> {
        sideMenu.setVisible(false);
        sideMenu.setManaged(false);
        overlay.setMouseTransparent(true);
        overlay.setStyle(null);
        showDashboard();
    });

    contact.setOnMouseClicked(e -> {
        sideMenu.setVisible(false);
        sideMenu.setManaged(false);
        overlay.setMouseTransparent(true);
        overlay.setStyle(null);
        openContactUsPage();
    });

    about.setOnMouseClicked(e -> {
        sideMenu.setVisible(false);
        sideMenu.setManaged(false);
        overlay.setMouseTransparent(true);
        overlay.setStyle(null);
        openAboutUsPage();
    });

    Scene scene = new Scene(root, 1380, 700);
    window.setTitle("Nutrix Dashboard");
    window.setScene(scene);
}

    // Feature button factory for dashboard
    private Button createFeatureButton(String icon, String title, String color) {
        Button btn = new Button();
        VBox box = new VBox(10);
        box.setPrefSize(250, 100);
        box.setAlignment(Pos.CENTER);
        btn.setGraphic(box);
        btn.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12; -fx-cursor: hand;");

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(38));
        iconLabel.setTextFill(Color.WHITE);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setTextFill(Color.WHITE);

        box.getChildren().addAll(iconLabel, titleLabel);

        return btn;
    }

    private VBox createImageBlock(String path, String title) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(250, 180);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

        ImageView imageView = new ImageView(new Image(path));
        imageView.setFitWidth(220);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        Label label = new Label(title);
        label.setFont(Font.font("Arial", 15));
        label.setTextFill(Color.BLACK);

        box.getChildren().addAll(imageView, label);
        return box;
    }

    private Node createBackArrow() {
        Button back = new Button("<");
        back.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #FFBA00;");
        back.setOnAction(e -> showMainMenuPage());

        HBox box = new HBox(back);
        box.setAlignment(Pos.TOP_LEFT);
        box.setPadding(new Insets(10));
        return box;
    }

    private Label createWhiteLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        return label;
    }

    private TextField createInputBox() {
        TextField tf = new TextField();
        tf.setMaxWidth(280);
        tf.setStyle("-fx-background-radius: 8px; -fx-padding: 10 12; -fx-font-size: 16px;");
        return tf;
    }

    private PasswordField createPasswordBox() {
        PasswordField pf = new PasswordField();
        pf.setMaxWidth(280);
        pf.setStyle("-fx-background-radius: 8px; -fx-padding: 10 12; -fx-font-size: 16px;");
        return pf;
    }

    private void highlightButton(Button btn) {
        btn.setStyle("-fx-background-color: #B46617; -fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 12 40;");
    }

    private void unhighlightButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #B46617; -fx-font-size: 19px; -fx-font-weight: bold;");
    }

    private void addHoverEffect(ImageView imageView) {
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        imageView.setOnMouseEntered(e -> {
            imageView.setScaleX(1.1);
            imageView.setScaleY(1.1);
        });
        imageView.setOnMouseExited(e -> {
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });
    }

    // ==================== ADDITIONAL PAGES & DIALOGS ========================

    // ---- DIET PLAN PAGE ----
    static class DietPlanDetails {
        String name;
        String duration;
        String earlyMorning;
        String breakfast;
        String midMorning;
        String lunch;
        String evening;
        String dinner;
        String advice;
        String color;

        DietPlanDetails(String name, String duration, String earlyMorning, String breakfast, String midMorning,
                        String lunch, String evening, String dinner, String advice, String color) {
            this.name = name;
            this.duration = duration;
            this.earlyMorning = earlyMorning;
            this.breakfast = breakfast;
            this.midMorning = midMorning;
            this.lunch = lunch;
            this.evening = evening;
            this.dinner = dinner;
            this.advice = advice;
            this.color = color;
        }
    }

    private final DietPlanDetails[] healthConditions = new DietPlanDetails[]{
        new DietPlanDetails("Weight Loss", "Duration: 6 months", "Warm lemon water", "Oats with berries", "Green tea",
                "Grilled chicken salad", "Apple slices", "Steamed vegetables", "Maintain calorie deficit, exercise regularly.", "#035752"),
        new DietPlanDetails("Weight Gain", "Duration: 3 months", "Milkshake", "Peanut butter toast", "Trail mix",
                "Brown rice with chicken", "Banana", "Pasta with veggies", "Increase calorie intake with nutrient dense food.", "#035752"),
        new DietPlanDetails("Diabetes Management", "Duration: Ongoing", "Cinnamon water", "Whole grain cereal", "Nuts",
                "Vegetable curry with brown rice", "Low-fat yogurt", "Grilled fish", "Monitor carbs, avoid sugary foods.", "#035752"),
          new DietPlanDetails("Heart Health", "Duration: Ongoing", "Green tea", "Oatmeal with fruits", "Almonds",
                "Grilled salmon with greens", "Carrot sticks", "Quinoa salad", "Limit saturated fat, increase omega-3.", "#035752"),
        new DietPlanDetails("Muscle Building", "Duration: 6 months", "Protein shake", "Eggs and toast", "Mixed nuts",
                "Steak with veggies", "Greek yogurt", "Chicken and sweet potatoes", "Focus on high protein and strength training.", "#035752"),
        new DietPlanDetails("Detoxification", "Duration: 1 month", "Lemon water", "Fruit smoothie", "Herbal tea",
                "Light soup", "Cucumber slices", "Steamed veggies", "Avoid processed foods, hydrate well.", "#035752"),
        new DietPlanDetails("Balanced Diet", "Duration: Ongoing", "Water with lemon", "Mixed fruit bowl", "Yogurt",
                "Veggie stir fry with tofu", "Nuts", "Brown rice and beans", "Eat a variety of food groups daily.", "#035752"),
        new DietPlanDetails("Low Sodium", "Duration: Ongoing", "Water", "Oatmeal with banana", "Fresh fruit",
                "Grilled chicken salad", "Celery sticks", "Steamed fish", "Avoid salt and processed foods.", "#035752"),
        new DietPlanDetails("Cholesterol Control", "Duration: Ongoing", "Green tea", "Whole grain toast", "Almonds",
                "Vegetable curry", "Low-fat cheese", "Grilled fish", "Limit saturated fats, eat fiber rich foods.", "#035752"),
        new DietPlanDetails("Hydration", "Duration: Ongoing", "Water with lemon", "Watermelon slices", "Coconut water",
                "Salad with cucumber", "Herbal tea", "Vegetable soup", "Drink at least 8 glasses daily.", "#035752"),
        new DietPlanDetails("High Blood Pressure", "Duration: Ongoing", "Beetroot juice", "Whole grain toast", "Mixed nuts",
                "Steamed veggies with grilled chicken", "Green tea", "Lentil soup", "Reduce sodium intake and exercise regularly.", "#035752"),
        new DietPlanDetails("Thyroid Health", "Duration: Ongoing", "Warm water with turmeric", "Egg whites and spinach", "Almonds",
                "Grilled fish with veggies", "Herbal tea", "Quinoa salad", "Include iodine and selenium rich foods.", "#035752"),
        new DietPlanDetails("Kidney Health", "Duration: Ongoing", "Cranberry juice", "Oats with berries", "Apple slices",
                "Grilled chicken salad", "Water", "Steamed vegetables", "Limit sodium and protein intake.", "#035752"),
        new DietPlanDetails("Liver Health", "Duration: Ongoing", "Lemon water", "Vegetable smoothie", "Walnuts",
                "Brown rice with veggies", "Green tea", "Fish with steamed broccoli", "Avoid alcohol and fatty foods.", "#035752"),
        new DietPlanDetails("Gut Health", "Duration: Ongoing", "Bone broth", "Yogurt with flaxseed", "Papaya",
                "Quinoa salad", "Herbal tea", "Vegetable stir-fry", "Include probiotics and fiber.", "#035752"),
        new DietPlanDetails("Bone Health", "Duration: Ongoing", "Orange juice", "Scrambled eggs with spinach", "Almonds",
                "Grilled chicken", "Low-fat milk", "Lentil soup", "Increase calcium and vitamin D.", "#035752"),
        new DietPlanDetails("Vision Health", "Duration: Ongoing", "Carrot juice", "Smoothie with spinach and berries", "Pumpkin seeds",
                "Salmon with veggies", "Green tea", "Sweet potato mash", "Eat foods rich in vitamin A and antioxidants.", "#035752"),
        new DietPlanDetails("Brain Health", "Duration: Ongoing", "Green tea", "Oats with walnuts", "Blueberries",
                "Grilled salmon", "Herbal tea", "Vegetable stew", "Increase omega-3 intake and antioxidants.", "#035752"),
        new DietPlanDetails("Anemia", "Duration: Ongoing", "Beetroot juice", "Whole grain cereal", "Dates",
                "Spinach and lentil curry", "Black tea", "Red meat with veggies", "Include iron-rich foods and vitamin C.", "#035752"),
        new DietPlanDetails("Immune Support", "Duration: Ongoing", "Warm lemon water", "Mixed fruits bowl", "Nuts",
                "Chicken soup", "Herbal tea", "Veggie stir fry", "Increase vitamin C and zinc intake.", "#035752"),
        new DietPlanDetails("Stress Management", "Duration: Ongoing", "Chamomile tea", "Oatmeal with honey", "Almonds",
                "Grilled fish with veggies", "Green tea", "Vegetable soup", "Include magnesium rich foods.", "#035752"),
        new DietPlanDetails("Sleep Improvement", "Duration: Ongoing", "Warm milk with turmeric", "Banana and oats", "Walnuts",
                "Light dinner with vegetables", "Herbal tea", "Chamomile tea", "Avoid caffeine late, manage screen time.", "#035752"),
        new DietPlanDetails("Arthritis Relief", "Duration: Ongoing", "Ginger tea", "Oats with berries", "Mixed nuts",
                "Grilled chicken salad", "Green tea", "Steamed veggies", "Include anti-inflammatory foods.", "#035752"),
        new DietPlanDetails("Allergy Management", "Duration: Ongoing", "Honey water", "Fruit salad", "Seeds",
                "Veggie stir fry", "Peppermint tea", "Rice and vegetables", "Avoid allergens and include vitamin C.", "#035752"),
        new DietPlanDetails("PCOS Management", "Duration: Ongoing", "Cinnamon water", "Egg white omelette", "Berries",
                "Grilled chicken salad", "Green tea", "Vegetable soup", "Balance carbs with protein intake.", "#035752"),
        new DietPlanDetails("Menopause Support", "Duration: Ongoing", "Soy milk smoothie", "Whole grain cereal", "Almonds",
                "Vegetable curry", "Green tea", "Tofu stir fry", "Include phytoestrogens and calcium.", "#035752"),
        new DietPlanDetails("Pregnancy Nutrition", "Duration: 9 months", "Warm water with lemon", "Oats with fruit", "Nuts",
                "Chicken and vegetable stew", "Milk", "Rice and lentils", "Increase folate, iron, calcium intake.", "#035752"),
        new DietPlanDetails("Postpartum Nutrition", "Duration: 3 months", "Milkshake", "Eggs and toast", "Bananas",
                "Vegetable curry with rice", "Herbal tea", "Chicken soup", "Eat nutrient-dense, protein rich foods.", "#035752"),
        new DietPlanDetails("Child Nutrition", "Duration: Ongoing", "Milk with honey", "Fruits and cereal", "Nuts",
                "Rice, beans, and veggies", "Water", "Steamed vegetables", "Balanced diet with essential nutrients.", "#035752"),
        new DietPlanDetails("Elderly Nutrition", "Duration: Ongoing", "Warm water with lemon", "Oatmeal with fruits", "Almonds",
                "Grilled fish with veggies", "Herbal tea", "Vegetable stew", "Include calcium and vitamin D.", "#035752"),
        new DietPlanDetails("Cancer Support", "Duration: Ongoing", "Green tea", "Vegetable smoothie", "Nuts",
                "Baked fish with veggies", "Herbal tea", "Quinoa salad", "Avoid processed foods, maintain nutrition.", "#035752"),
        new DietPlanDetails("Liver Detox", "Duration: 2 weeks", "Lemon water", "Green smoothie", "Herbal tea",
                "Light vegetable soup", "Water", "Steamed vegetables", "Avoid alcohol and processed foods.", "#035752"),
        new DietPlanDetails("Kidney Detox", "Duration: 2 weeks", "Cucumber water", "Fruits", "Nuts",
                "Brown rice with steamed veggies", "Herbal tea", "Lentil soup", "Limit sodium and protein.", "#035752"),
        new DietPlanDetails("Gut Detox", "Duration: 2 weeks", "Bone broth", "Yogurt", "Fruit",
                "Vegetable soup", "Herbal tea", "Steamed veggies", "Probiotics and fiber rich food.", "#035752"),
        new DietPlanDetails("Lung Health", "Duration: Ongoing", "Green tea", "Oatmeal with berries", "Almonds",
                "Grilled chicken salad", "Herbal tea", "Vegetable soup", "Avoid smoke and toxins, increase antioxidants.", "#035752"),
        new DietPlanDetails("Skin Health", "Duration: Ongoing", "Watermelon juice", "Fruits and nuts", "Seeds",
                "Grilled fish with salads", "Herbal tea", "Mixed vegetable stir fry", "Hydrate and consume antioxidants.", "#035752"),
        new DietPlanDetails("Diabetic Foot Care", "Duration: Ongoing", "Cinnamon tea", "Whole grain cereal", "Nuts",
                "Vegetable curry with brown rice", "Green tea", "Grilled fish", "Maintain blood sugar and foot hygiene.", "#035752"),
        new DietPlanDetails("Throat Care", "Duration: Ongoing", "Warm lemon water with honey", "Oats with fruits", "Herbal tea",
                "Steamed vegetables", "Green tea", "Light soup", "Avoid irritants and stay hydrated.", "#035752"),
        new DietPlanDetails("Brain Fog Relief", "Duration: Ongoing", "Green tea", "Berries and nuts", "Protein shake",
                "Grilled salmon with veggies", "Herbal tea", "Vegetable stew", "Increase omega-3 and antioxidants.", "#035752"),
        new DietPlanDetails("Anti-Aging", "Duration: Ongoing", "Green smoothie", "Oats with fruits", "Nuts",
                "Grilled chicken salad", "Herbal tea", "Steamed veggies", "Consume antioxidants and healthy fats.", "#035752"),
        new DietPlanDetails("Energy Boost", "Duration: Ongoing", "Coffee with cinnamon", "Eggs and toast", "Fruit",
                "Chicken and vegetable stir fry", "Green tea", "Oatmeal", "Balanced meals with complex carbs and protein.", "#035752"),
        new DietPlanDetails("Cognitive Health", "Duration: Ongoing", "Herbal tea", "Nuts and berries", "Protein shake",
                "Salmon with veggies", "Green tea", "Vegetable soup", "Increase omega-3 fatty acids and antioxidants.", "#035752"),
        new DietPlanDetails("Inflammation Reduction", "Duration: Ongoing", "Ginger tea", "Berries and oats", "Almonds",
                "Grilled fish with veggies", "Green tea", "Vegetable stew", "Include anti-inflammatory foods.", "#035752"),
        new DietPlanDetails("Hormonal Balance", "Duration: Ongoing", "Cinnamon water", "Egg white omelette", "Berries",
                "Grilled chicken salad", "Green tea", "Vegetable soup", "Balance sugars and improve insulin sensitivity.", "#035752"),
        
    };
       

    private String darkenColor(String hexColor, double factor) {
        Color color = Color.web(hexColor);
        Color darker = color.deriveColor(0, 1, factor, 1);
        return String.format("#%02X%02X%02X",
                (int) (darker.getRed() * 255),
                (int) (darker.getGreen() * 255),
                (int) (darker.getBlue() * 255));
    }

    private VBox createPlanContent(DietPlanDetails dp) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);
        content.setMaxWidth(480);
        content.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.55);" +
                        "-fx-background-radius: 12;"
        );

        Text durationLabel = new Text(dp.duration);
        durationLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        durationLabel.setFill(Color.web("#F0EAD6"));

        content.getChildren().add(durationLabel);

        content.getChildren().add(createSection("Early Morning:", dp.earlyMorning));
        content.getChildren().add(createSection("Breakfast:", dp.breakfast));
        content.getChildren().add(createSection("Mid-Morning:", dp.midMorning));
        content.getChildren().add(createSection("Lunch:", dp.lunch));
        content.getChildren().add(createSection("Evening:", dp.evening));
        content.getChildren().add(createSection("Dinner:", dp.dinner));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #FFFFFF33;");

        Text adviceLabel = new Text("Important Advice:");
        adviceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        adviceLabel.setFill(Color.web("#F0EAD6"));

        Text adviceText = new Text(dp.advice);
        adviceText.setFont(Font.font("Segoe UI", 16));
        adviceText.setFill(Color.web("#DDD9CC"));
        adviceText.setWrappingWidth(440);

        content.getChildren().addAll(sep, adviceLabel, adviceText);

        return content;
    }

    private VBox createSection(String heading, String detail) {
        Text headingText = new Text(heading);
        headingText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        headingText.setFill(Color.web("#F0EAD6"));

        Text detailText = new Text(detail);
        detailText.setFont(Font.font("Segoe UI", 15));
        detailText.setFill(Color.web("#DDD9CC"));
        detailText.setWrappingWidth(440);

        return new VBox(5, headingText, detailText);
    }

    private void openDietPlanPage() {
        Stage stage = new Stage();
        stage.setTitle("Diet Plans");

        Label heading = new Label("Choose a Health Condition");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#F0EAD6"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        String[] icons = {
                "⚖️", "⬆️", "💉", "❤️", "💪",
                "🍃", "🥗", "🧂", "🩺", "💧",
                "💊", "🥬", "🍇", "🥚", "🍵",
                "🍏", "🧀", "🥕", "🧄", "🥔",
                "🌞", "🥛", "🍒", "🥩", "🍋",
                "🌿", "🥤", "🍉", "🥑", "🫐",
                "🍄", "🌰", "🍠", "🍗", "🥥",
                "🍊", "🍳", "🥒", "🍖", "🍓",
                "🍍", "🥭", "🍈", "🍋", "🍅",
                "🥝", "🍑", "🍏", "🍇", "🍊",
                "🍉", "🍌", "🍒", "🍍", "🍑",
                "🍈", "🥭", "🍋", "🍊", "🍏",
                "🍇", "🍓", "🍉", "🍌", "🍒",
                "🍍", "🥭", "🍈", "🍋", "🍅",
                "🥝", "🍑", "🍏", "🍇", "🍊",
                "🍉", "🍌", "🍒", "🍍", "🍑"
        };

        for (int i = 0; i < healthConditions.length; i++) {
            DietPlanDetails dp = healthConditions[i];
            Button btn = new Button(icons[i] + "  " + dp.name);
            btn.setPrefWidth(280);
            btn.setPrefHeight(50);
            btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            btn.setStyle("-fx-background-color: " + dp.color + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 15;" +
                    "-fx-cursor: hand;");

            btn.setOnAction(e -> showDietDialog(dp));

            grid.add(btn, i % 2, i / 2);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(820);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "  -fx-background: transparent;" +
                        "  -fx-background-color: transparent;" +
                        "  -fx-padding: 0;" +
                        "  -fx-control-inner-background: transparent;" +
                        "  -fx-background-insets: 0;" +
                        "  -fx-background-radius: 0;"
        );

        scrollPane.lookupAll(".scroll-bar").forEach(sb -> {
            sb.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pref-width: 6;" +
                            "-fx-padding: 0;"
            );
        });

        VBox root = new VBox(30, heading, scrollPane);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0A2F28;");
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 820, 900));
        stage.show();
    }

    private void showDietDialog(DietPlanDetails dp) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(dp.name);
        dialog.setHeaderText(dp.name + " Plan");

        DialogPane dialogPane = dialog.getDialogPane();

        String base = dp.color;
        String dark = darkenColor(base, 0.6);
        dialogPane.setStyle(
                "-fx-background-radius: 15;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: linear-gradient(to bottom right, " + dark + ", " + base + ");"
        );

        VBox content = createPlanContent(dp);
        dialogPane.setContent(content);

        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: transparent;");
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setTextFill(Color.web("#F0EAD6"));
            headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        }

        dialogPane.getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

        String okBase = "#F0EAD6";
        String okHover = "#D6C9A8";

        String okBaseStyle =
                "-fx-background-color: " + okBase + ";" +
                        "-fx-text-fill: " + dp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);";

        String okHoverStyle =
                "-fx-background-color: " + okHover + ";" +
                        "-fx-text-fill: " + dp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 3);";

        okButton.setStyle(okBaseStyle);
        okButton.setOnMouseEntered(ev -> okButton.setStyle(okHoverStyle));
        okButton.setOnMouseExited(ev -> okButton.setStyle(okBaseStyle));

        VBox.setMargin(okButton, new Insets(20, 0, 0, 0));

        dialog.showAndWait();
    }

    // ---- WORKOUT PLAN PAGE ----

    static class WorkoutPlanDetails {
        String name;
        String planText;
        String color;

        WorkoutPlanDetails(String name, String planText, String color) {
            this.name = name;
            this.planText = planText;
            this.color = color;
        }
    }

   private final WorkoutPlanDetails[] workoutPlans = new WorkoutPlanDetails[]{
            new WorkoutPlanDetails("Weight Loss",
                    "Monday: Chest & Back\n" +
                            "- Weighted Pull-ups: 4 sets x 6 reps (add weight)\n" +
                            "- Incline Bench Press: 4 sets x 6 reps (80-85% 1RM)\n" +
                            "- Deadlifts: 5 sets x 5 reps (heavy)\n" +
                            "- Bent-over Rows: 3 sets x 8 reps\n\n" +
                            "Tuesday: Legs\n" +
                            "- Back Squats: 5 sets x 5 reps (heavy)\n" +
                            "- Front Squats: 3 sets x 8 reps\n" +
                            "- Romanian Deadlifts: 3 sets x 8 reps\n" +
                            "- Calf Raises: 4 sets x 15 reps\n\n" +
                            "Thursday: Shoulders & Arms\n" +
                            "- Overhead Press: 5 sets x 5 reps\n" +
                            "- Weighted Dips: 3 sets x 8 reps\n" +
                            "- Barbell Curls: 3 sets x 8 reps\n" +
                            "- Tricep Extensions: 3 sets x 10 reps\n\n" +
                            "Friday: HIIT\n" +
                            "- 20 minute intense intervals (30s on/30s off)\n\n" +
                            "Saturday: Strongman Training\n" +
                            "- Tire Flips: 3 sets x 10 reps\n" +
                            "- Sled Push: 3 sets x 30 seconds\n" +
                            "- Battle Ropes: 3 sets x 30 seconds\n\n" +
                            "Sunday: Active Recovery"
                    , "#035752"),
            new WorkoutPlanDetails("Weight Gain",
                    "Monday: Chest & Triceps\n" +
                            "- Bench Press: 4 sets x 8 reps (75-80% 1RM)\n" +
                            "- Incline Dumbbell Press: 3 sets x 10 reps\n" +
                            "- Tricep Dips: 3 sets x 12 reps (add weight if needed)\n" +
                            "Tuesday: Back & Biceps\n" +
                            "- Pull-ups: 4 sets x 8 reps (weighted if possible)\n" +
                            "- Bent-over Rows: 3 sets x 10 reps\n" +
                            "- Barbell Curls: 3 sets x 12 reps\n" +
                            "Wednesday: Legs\n" +
                            "- Squats: 4 sets x 8 reps\n" +
                            "- Leg Press: 3 sets x 10 reps\n" +
                            "- Leg Curls: 3 sets x 12 reps\n" +
                            "Thursday: Shoulders & Abs\n" +
                            "- Military Press: 4 sets x 8 reps\n" +
                            "- Lateral Raises: 3 sets x 12 reps\n" +
                            "- Plank: 3 sets x 60 seconds\n" +
                            "Friday: Full Body\n" +
                            "- Deadlifts: 4 sets x 6 reps\n" +
                            "- Clean and Press: 3 sets x 8 reps\n" +
                            "- Kettlebell Swings: 3 sets x 15 reps\n\n" +
                            "Weekend: Rest or Active Recovery\n\n" +
                            "Nutrition Tips:\n" +
                            "• Aim for 1g protein per pound of body weight\n" +
                            "• Eat at 300-500 calorie surplus\n" +
                            "• Include healthy fats (nuts, avocados)"
                    , "#035752"),
            new WorkoutPlanDetails("Diabetes Management",
                    "Monday: Cardio & Strength\n" +
                            "- Resistance band exercises: 3 sets x 12 reps\n" +
                            "- Bodyweight squats: 3 sets x 15 reps\n\n" +
                            "Tuesday: Flexibility\n" +
                            "- Yoga session: 30 mins (focus on poses that aid circulation)\n" +
                            "- Tai Chi: 20 mins for balance\n\n" +
                            "Wednesday: Resistance Training\n" +
                            "- Dumbbell shoulder press: 3 sets x 10 reps\n" +
                            "- Stability ball exercises: 15 mins\n\n" +
                            "Thursday: Aerobic\n" +
                            "- Swimming: 30 mins (low impact)\n" +
                            "- Stationary bike: 20 mins (moderate resistance)\n\n" +
                            "Friday: Core & Balance\n" +
                            "- Bosu ball exercises: 15 mins\n" +
                            "- Leg raises: 3 sets x 12 reps\n\n" +
                            "Weekend: Active Recovery\n" +
                            "- Light walking: 30 mins daily\n" +
                            "Important Notes:\n" +
                            "• Monitor blood sugar before/after exercise\n" +
                            "• Stay hydrated\n" +
                            "• Carry fast-acting carbs during workouts"
                    , "#035752"),
            new WorkoutPlanDetails("Heart Health",
                    "Daily: Cardio Foundation\n" +
                            "- Brisk walking: 30-45 mins (keep heart rate at 50-70% max)\n" +
                            "- Cycling: 20 mins (optional)\n\n" +
                            "Monday/Wednesday/Friday: Strength\n" +
                            "- Light dumbbell exercises: 3 sets x 12 reps\n" +
                            "- Resistance band training: 15 mins\n" +
                            "- Bodyweight exercises (push-ups, squats): 3 sets\n\n" +
                            "Tuesday/Thursday: Flexibility\n" +
                            "- Yoga for heart health: 30 mins\n" +
                            "- Deep breathing exercises: 10 mins\n\n" +
                            "Saturday: Active Recovery\n" +
                            "- Leisure swimming: 30 mins\n" +
                            "- Gentle stretching: 15 mins\n\n" +
                            "Sunday: Rest or Light Activity\n\n" +
                            "Key Considerations:\n" +
                            "• Maintain moderate intensity\n" +
                            "• Include warm-up/cool-down periods\n" +
                            "• Monitor heart rate during exercise"
                    , "#035752"),
            new WorkoutPlanDetails("Muscle Building",
                    "Monday: Chest & Triceps (Heavy)\n" +
                            "- Flat bench press: 5 sets x 5 reps (85% 1RM)\n" +
                            "- Incline dumbbell press: 4 sets x 8 reps\n" +
                            "- Weighted dips: 4 sets x 8 reps\n" +
                            "Tuesday: Back & Biceps (Volume)\n" +
                            "- Pull-ups: 5 sets x max reps\n" +
                            "- Barbell rows: 4 sets x 8 reps\n" +
                            "- Deadlifts: 5 sets x 5 reps\n" +
                            "Wednesday: Legs (Power)\n" +
                            "- Back squats: 5 sets x 5 reps\n" +
                            "- Front squats: 3 sets x 8 reps\n" +
                            "- Romanian deadlifts: 4 sets x 8 reps\n" +
                            "Thursday: Shoulders & Traps\n" +
                            "- Overhead press: 5 sets x 5 reps\n" +
                            "- Lateral raises: 4 sets x 12 reps\n" +
                            "- Shrugs: 5 sets x 10 reps (heavy)\n" +
                            "- Face pulls: 4 sets x 15 reps\n\n" +
                            "Friday: Full Body (Hypertrophy)\n" +
                            "- Clean and press: 4 sets x 8 reps\n" +
                            "- Kettlebell swings: 3 sets x 20 reps\n" +
                            "- Farmer's walk: 3 sets x 30 sec\n\n" +
                            "Weekend: Active Recovery\n" +
                            "- Light cardio: 20 mins\n" +
                            "- Mobility work: 15 mins"
                    , "#035752"),
            new WorkoutPlanDetails("Detoxification",
                    "Daily: Lymphatic Stimulation\n" +
                            "- Rebounding (mini trampoline): 15 mins\n" +
                            "- Dry brushing before shower\n\n" +
                            "Morning Routine:\n" +
                            "- Yoga twists: 10 mins\n" +
                            "- Deep breathing: 5 mins\n\n" +
                            "Monday/Wednesday/Friday: Light Cardio\n" +
                            "- Walking in nature: 30 mins\n" +
                            "- Swimming: 20 mins (optional)\n\n" +
                            "Tuesday/Thursday: Gentle Strength\n" +
                            "- Bodyweight exercises: 3 sets x 12 reps\n" +
                            "- Resistance band work: 15 mins\n\n" +
                            "Saturday: Sauna Session\n" +
                            "- 20-30 mins with cool showers between\n\n" +
                            "Sunday: Complete Rest\n" +
                            "- Meditation: 20 mins\n" +
                            "- Stretching: 10 mins\n\n" +
                            "Detox Tips:\n" +
                            "• Stay hydrated with lemon water\n" +
                            "• Epsom salt baths 2-3x/week\n" +
                            "• Avoid strenuous exercise during detox"
                    , "#035752"),
            new WorkoutPlanDetails("Balanced Diet",
                    "Monday: Full Body Strength\n" +
                            "- Squats: 3 sets x 12 reps\n" +
                            "- Push-ups: 3 sets x max reps\n" +
                            "- Bent-over rows: 3 sets x 12 reps\n" +
                            "Tuesday: Cardio & Core\n" +
                            "- Jogging: 25 mins\n" +
                            "- Bicycle crunches: 3 sets x 20 reps\n" +
                            "Wednesday: Active Recovery\n" +
                            "- Yoga flow: 30 mins\n" +
                            "- Walking: 30 mins\n\n" +
                            "Thursday: HIIT\n" +
                            "- Circuit training: 30 sec work/30 sec rest\n" +
                            "- Exercises: Jump squats, burpees, mountain climbers\n" +
                            "- Total time: 20 mins\n\n" +
                            "Friday: Strength Endurance\n" +
                            "- Dumbbell complex: 3 rounds\n" +
                            "- (Clean, press, squat, row)\n" +
                            "Weekend: Variety\n" +
                            "- Choose fun activity (hiking, sports, dancing)\n" +
                            "- Stretching session: 20 mins\n\n" +
                            "Nutrition Pairing:\n" +
                            "• Protein post-workout\n" +
                            "• Complex carbs for energy\n" +
                            "• Hydrate consistently"
                    , "#035752"),
            new WorkoutPlanDetails("Low Sodium",
                    "Daily: Circulation Focus\n" +
                            "- Walking: 30 mins (promotes fluid balance)\n" +
                            "- Leg elevation exercises: 10 mins\n\n" +
                            "Monday/Wednesday/Friday: Light Strength\n" +
                            "- Resistance band exercises: 3 sets x 15 reps\n" +
                            "- Bodyweight movements: 3 sets x 12 reps\n" +
                            "- Focus on full range of motion\n\n" +
                            "Tuesday/Thursday: Water-Based\n" +
                            "- Swimming: 30 mins\n" +
                            "- Water aerobics: 20 mins\n\n" +
                            "Saturday: Mobility Work\n" +
                            "- Yoga for fluid balance: 30 mins\n" +
                            "- Foam rolling: 15 mins\n\n" +
                            "Sunday: Rest & Recovery\n" +
                            "- Gentle stretching: 15 mins\n" +
                            "- Deep breathing: 10 mins\n\n" +
                            "Key Considerations:\n" +
                            "• Monitor hydration carefully\n" +
                            "• Avoid excessive sweating\n" +
                            "• Include potassium-rich foods post-workout"
                    , "#035752"),
             new WorkoutPlanDetails("Cholesterol Control", 
            "Daily: Aerobic Foundation\n" +
            "- Brisk walking: 45 mins\n" +
            "- Cycling: 20 mins (optional)\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Circuit training: 3 rounds\n" +
            "- Squats, push-ups, rows, planks\n" +
            "- Moderate weights, higher reps (12-15)\n\n" +
            "Tuesday/Thursday: Interval Training\n" +
            "- Walk/jog intervals: 30 sec fast/90 sec slow\n" +
            "- Total time: 25 mins\n\n" +
            "Saturday: Active Lifestyle Day\n" +
            "- Gardening, housework, leisure activities\n" +
            "- Standing desk work if possible\n\n" +
            "Sunday: Yoga & Relaxation\n" +
            "- Yoga for heart health: 30 mins\n" +
            "- Meditation: 10 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Raises HDL cholesterol\n" +
            "• Lowers LDL cholesterol\n" +
            "• Improves circulation",
            "#035752"),

        new WorkoutPlanDetails("Hydration", 
            "Morning Routine:\n" +
            "- Yoga for kidney health: 20 mins\n" +
            "- Lemon water consumption\n\n" +
            "Daily: Moderate Movement\n" +
            "- Walking: 30 mins (with water bottle)\n" +
            "- Stretching: 10 mins every 2 hours\n\n" +
            "Monday/Wednesday/Friday: Light Strength\n" +
            "- Bodyweight exercises: 3 sets x 12 reps\n" +
            "- Resistance band work: 15 mins\n\n" +
            "Tuesday/Thursday: Water-Based\n" +
            "- Swimming: 30 mins\n" +
            "- Aqua jogging: 20 mins\n\n" +
            "Weekend: Recovery Focus\n" +
            "- Electrolyte-replenishing activities\n" +
            "- Meditation for thirst awareness\n\n" +
            "Hydration Tips:\n" +
            "• Drink before, during, after exercise\n" +
            "• Monitor urine color\n" +
            "• Include water-rich foods",
            "#035752"),

        new WorkoutPlanDetails("High Blood Pressure", 
            "Daily: Gentle Cardio\n" +
            "- Walking: 30-40 mins\n" +
            "- Stationary bike: 20 mins (optional)\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Light resistance training: 2 sets x 12 reps\n" +
            "- Focus on breathing control\n" +
            "- Avoid heavy weights\n\n" +
            "Tuesday/Thursday: Relaxation\n" +
            "- Yoga for BP control: 30 mins\n" +
            "- Deep breathing: 10 mins\n\n" +
            "Saturday: Leisure Activity\n" +
            "- Gardening, golf (no cart), tai chi\n\n" +
            "Sunday: Complete Rest\n" +
            "- Meditation: 20 mins\n" +
            "- Gentle stretching: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Avoid holding breath during exercise\n" +
            "• No heavy lifting\n" +
            "• Monitor BP regularly",
            "#035752"),

        new WorkoutPlanDetails("Thyroid Health", 
            "Morning Routine:\n" +
            "- Neck stretches: 5 mins\n" +
            "- Sun salutations: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Moderate weight training: 3 sets x 10 reps\n" +
            "- Focus on compound movements\n" +
            "- Include selenium-supportive exercises\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Swimming: 20 mins (optional)\n\n" +
            "Saturday: Stress Reduction\n" +
            "- Yoga for thyroid: 30 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Sunday: Active Recovery\n" +
            "- Light gardening or walking\n" +
            "- Foam rolling: 10 mins\n\n" +
            "Exercise Tips:\n" +
            "• Avoid overtraining\n" +
            "• Include cooling exercises if hyperthyroid\n" +
            "• Gentle stimulation for hypothyroid",
            "#035752"),

        new WorkoutPlanDetails("Kidney Health", 
            "Daily: Gentle Movement\n" +
            "- Walking: 20-30 mins\n" +
            "- Seated exercises: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Light Strength\n" +
            "- Resistance bands: 2 sets x 12 reps\n" +
            "- Bodyweight exercises: 2 sets x 10 reps\n\n" +
            "Tuesday/Thursday: Flexibility\n" +
            "- Yoga for kidney health: 25 mins\n" +
            "- Tai chi: 15 mins\n\n" +
            "Saturday: Water-Based\n" +
            "- Swimming: 20 mins\n" +
            "- Aqua stretching: 10 mins\n\n" +
            "Sunday: Complete Rest\n" +
            "- Meditation: 15 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Avoid dehydration\n" +
            "• Monitor urine output\n" +
            "• No high-impact activities",
            "#035752"),

        new WorkoutPlanDetails("Liver Health", 
            "Morning Routine:\n" +
            "- Liver-stimulating yoga twists: 10 mins\n" +
            "- Dry brushing: 5 mins\n\n" +
            "Daily: Moderate Movement\n" +
            "- Walking: 30 mins\n" +
            "- Deep breathing: 5 mins every 2 hours\n\n" +
            "Monday/Wednesday/Friday: Core Work\n" +
            "- Plank variations: 3 sets x 30 sec\n" +
            "- Seated rotations: 3 sets x 12 reps/side\n" +
            "- Bicycle crunches: 3 sets x 15 reps\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Cycling: 25 mins\n" +
            "- Elliptical: 15 mins\n\n" +
            "Weekend: Detox Focus\n" +
            "- Rebounding: 15 mins\n" +
            "- Sauna: 20 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Promotes liver circulation\n" +
            "• Aids detoxification\n" +
            "• Reduces fatty deposits",
            "#035752"),

        new WorkoutPlanDetails("Gut Health", 
            "Morning Routine:\n" +
            "- Abdominal massage: 5 mins\n" +
            "- Yoga for digestion: 10 mins\n\n" +
            "Daily: Movement Breaks\n" +
            "- Walking after meals: 10 mins\n" +
            "- Seated twists: 5 mins every 2 hours\n\n" +
            "Monday/Wednesday/Friday: Core Strength\n" +
            "- Pelvic tilts: 3 sets x 15 reps\n" +
            "- Dead bugs: 3 sets x 12 reps\n" +
            "- Cat/cow stretches: 3 sets x 10 reps\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Dancing: 20 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Yoga nidra: 20 mins\n" +
            "- Gentle stretching: 15 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Stimulates peristalsis\n" +
            "• Reduces bloating\n" +
            "• Improves gut motility",
            "#035752"),

        new WorkoutPlanDetails("Bone Health", 
            "Daily: Weight-Bearing\n" +
            "- Walking: 30 mins\n" +
            "- Stair climbing: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Squats: 3 sets x 12 reps\n" +
            "- Lunges: 3 sets x 10 reps/leg\n" +
            "- Step-ups: 3 sets x 8 reps/leg\n\n" +
            "Tuesday/Thursday: Impact Training\n" +
            "- Gentle jumping jacks: 3 sets x 20 reps\n" +
            "- Heel drops: 3 sets x 15 reps\n" +
            "- Mini trampoline: 10 mins\n\n" +
            "Saturday: Resistance Training\n" +
            "- Resistance band exercises: 3 sets x 12 reps\n" +
            "- Bodyweight exercises: 3 sets x 10 reps\n\n" +
            "Sunday: Active Recovery\n" +
            "- Tai chi: 20 mins\n" +
            "- Stretching: 15 mins\n\n" +
            "Key Nutrients:\n" +
            "• Calcium-rich foods post-workout\n" +
            "• Vitamin D synthesis from sunlight\n" +
            "• Magnesium for recovery",
            "#035752"),

        new WorkoutPlanDetails("Vision Health", 
            "Morning Routine:\n" +
            "- Eye yoga: 10 mins\n" +
            "- Palming: 5 mins\n\n" +
            "Daily: Circulation Boosters\n" +
            "- Inversion poses: 3 mins\n" +
            "- Neck rotations: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Cycling: 20 mins\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Upper body focus: 3 sets x 12 reps\n" +
            "- Posture exercises: 15 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Eye massage: 5 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Improves ocular circulation\n" +
            "• Reduces eye strain\n" +
            "• Balances intraocular pressure",
            "#035752"),

        new WorkoutPlanDetails("Brain Health", 
            "Morning Routine:\n" +
            "- Aerobic exercise: 20 mins\n" +
            "- Coordination drills: 10 mins\n\n" +
            "Daily: Cognitive Movement\n" +
            "- Dance breaks: 5 mins every 2 hours\n" +
            "- Balancing exercises: 5 mins\n\n" +
            "Monday/Wednesday/Friday: HIIT\n" +
            "- Interval training: 20 mins\n" +
            "- Complex movements\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Compound lifts: 3 sets x 10 reps\n" +
            "- Unilateral exercises\n\n" +
            "Weekend: Novel Activities\n" +
            "- Learn new sport or dance\n" +
            "- Brain games while moving\n\n" +
            "Exercise Benefits:\n" +
            "• Increases BDNF production\n" +
            "• Improves neuroplasticity\n" +
            "• Enhances cognitive function",
            "#035752"),

        new WorkoutPlanDetails("Anemia", 
            "Daily: Gentle Movement\n" +
            "- Walking: 20-30 mins\n" +
            "- Deep breathing: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Light Strength\n" +
            "- Bodyweight exercises: 2 sets x 12 reps\n" +
            "- Resistance bands: 2 sets x 15 reps\n\n" +
            "Tuesday/Thursday: Flexibility\n" +
            "- Yoga: 25 mins\n" +
            "- Stretching: 10 mins\n\n" +
            "Saturday: Leisure Activity\n" +
            "- Gardening: 30 mins\n" +
            "- Gentle swimming: 20 mins\n\n" +
            "Sunday: Complete Rest\n" +
            "- Meditation: 15 mins\n" +
            "- Visualization: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Avoid overexertion\n" +
            "• Monitor energy levels\n" +
            "• Stay hydrated",
            "#035752"),

        new WorkoutPlanDetails("Immune Support", 
            "Daily: Moderate Movement\n" +
            "- Walking: 30 mins\n" +
            "- Lymphatic massage: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Bodyweight training: 3 sets x 12 reps\n" +
            "- Resistance exercises: 15 mins\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 25 mins\n" +
            "- Cycling: 20 mins\n\n" +
            "Saturday: Heat Therapy\n" +
            "- Sauna: 20 mins\n" +
            "- Contrast showers\n\n" +
            "Sunday: Rest & Recovery\n" +
            "- Meditation: 15 mins\n" +
            "- Gentle stretching: 10 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Increases circulation\n" +
            "• Supports lymphatic flow\n" +
            "• Reduces inflammation",
            "#035752"),

        new WorkoutPlanDetails("Stress Management", 
            "Morning Routine:\n" +
            "- Yoga: 20 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Daily: Movement Breaks\n" +
            "- Stretching: 5 mins every 2 hours\n" +
            "- Walking: 10 mins after meals\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Mindful weight training: 3 sets x 12 reps\n" +
            "- Focus on form and breathing\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Nature walks: 30 mins\n" +
            "- Swimming: 20 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Tai chi: 25 mins\n" +
            "- Meditation: 20 mins\n\n" +
            "Stress-Reducing Tips:\n" +
            "• Focus on enjoyment, not intensity\n" +
            "• Include social activities\n" +
            "• Avoid overtraining",
            "#035752"),

        new WorkoutPlanDetails("Sleep Improvement", 
            "Morning Routine:\n" +
            "- Sunlight exposure: 10 mins\n" +
            "- Light cardio: 15 mins\n\n" +
            "Daily: Movement Timing\n" +
            "- Exercise before 7pm\n" +
            "- Evening walks: 20 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Moderate weight training: 3 sets x 10 reps\n" +
            "- Finish by 6pm\n\n" +
            "Tuesday/Thursday: Yoga\n" +
            "- Restorative yoga: 30 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Gentle stretching: 20 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Sleep Tips:\n" +
            "• Cool down after exercise\n" +
            "• Avoid intense workouts before bed\n" +
            "• Establish routine",
            "#035752"),

        new WorkoutPlanDetails("Arthritis Relief", 
            "Daily: Gentle Movement\n" +
            "- Range of motion exercises: 10 mins\n" +
            "- Walking: 20 mins\n\n" +
            "Monday/Wednesday/Friday: Water Exercise\n" +
            "- Swimming: 30 mins\n" +
            "- Water aerobics: 20 mins\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Resistance bands: 2 sets x 12 reps\n" +
            "- Light weights: 2 sets x 10 reps\n\n" +
            "Saturday: Flexibility\n" +
            "- Yoga for arthritis: 25 mins\n" +
            "- Stretching: 15 mins\n\n" +
            "Sunday: Rest & Recovery\n" +
            "- Heat therapy: 15 mins\n" +
            "- Gentle massage\n\n" +
            "Important Notes:\n" +
            "• Avoid high-impact activities\n" +
            "• Modify as needed\n" +
            "• Listen to your body",
            "#035752"),

        new WorkoutPlanDetails("Allergy Management", 
            "Morning Routine:\n" +
            "- Nasal breathing exercises: 10 mins\n" +
            "- Indoor cycling: 15 mins\n\n" +
            "Daily: Controlled Environment\n" +
            "- HEPA-filtered gym workouts\n" +
            "- Avoid outdoor exercise during high pollen\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Resistance training: 3 sets x 12 reps\n" +
            "- Focus on breathing control\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Treadmill: 25 mins\n" +
            "- Elliptical: 15 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Steam room: 15 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Exercise Tips:\n" +
            "• Shower after outdoor activity\n" +
            "• Consider mask during high pollen\n" +
            "• Stay hydrated",
            "#035752"),

        new WorkoutPlanDetails("PCOS Management", 
            "Morning Routine:\n" +
            "- Sunlight exposure: 10 mins\n" +
            "- Walking: 15 mins\n\n" +
            "Monday/Wednesday/Friday: HIIT\n" +
            "- Interval training: 20 mins\n" +
            "- Bodyweight circuits\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Compound lifts: 3 sets x 10 reps\n" +
            "- Resistance training: 15 mins\n\n" +
            "Saturday: Yoga\n" +
            "- PCOS-focused yoga: 30 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Sunday: Active Recovery\n" +
            "- Leisure walking: 30 mins\n" +
            "- Stretching: 15 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Improves insulin sensitivity\n" +
            "• Reduces androgen levels\n" +
            "• Supports hormone balance",
            "#035752"),

        new WorkoutPlanDetails("Menopause Support", 
            "Morning Routine:\n" +
            "- Weight-bearing exercise: 15 mins\n" +
            "- Sunlight exposure: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Resistance training: 3 sets x 12 reps\n" +
            "- Focus on bone health\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Dancing: 20 mins\n\n" +
            "Saturday: Yoga\n" +
            "- Menopause yoga: 30 mins\n" +
            "- Cooling poses\n\n" +
            "Sunday: Recovery\n" +
            "- Foam rolling: 15 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Exercise Benefits:\n" +
            "• Reduces hot flashes\n" +
            "• Preserves bone density\n" +
            "• Improves sleep",
            "#035752"),

        new WorkoutPlanDetails("Pregnancy Nutrition", 
            "Daily: Gentle Movement\n" +
            "- Walking: 30 mins\n" +
            "- Pelvic floor exercises: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Prenatal Yoga\n" +
            "- Yoga for pregnancy: 30 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Tuesday/Thursday: Swimming\n" +
            "- Water exercise: 30 mins\n" +
            "- Aqua stretching: 10 mins\n\n" +
            "Saturday: Light Strength\n" +
            "- Bodyweight exercises: 2 sets x 12 reps\n" +
            "- Resistance bands: 15 mins\n\n" +
            "Sunday: Rest & Recovery\n" +
            "- Meditation: 15 mins\n" +
            "- Visualization: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Avoid supine position after 1st trimester\n" +
            "• Stay hydrated\n" +
            "• Listen to your body",
            "#035752"),

        new WorkoutPlanDetails("Postpartum Nutrition", 
            "Daily: Core Recovery\n" +
            "- Pelvic tilts: 3 sets x 12 reps\n" +
            "- Walking with baby: 20 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Postpartum-focused exercises\n" +
            "- Light resistance: 2 sets x 12 reps\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Stroller walks: 30 mins\n" +
            "- Dancing: 15 mins\n\n" +
            "Saturday: Yoga\n" +
            "- Postnatal yoga: 25 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Sunday: Recovery\n" +
            "- Self-massage: 15 mins\n" +
            "- Meditation: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Wait for doctor clearance\n" +
            "• Focus on pelvic floor\n" +
            "• Gradual progression",
            "#035752"),

        new WorkoutPlanDetails("Child Nutrition", 
            "Daily: Active Play\n" +
            "- Outdoor games: 60 mins\n" +
            "- Free play: 30 mins\n\n" +
            "Monday/Wednesday/Friday: Structured Activity\n" +
            "- Sports practice: 45 mins\n" +
            "- Swimming lessons: 30 mins\n\n" +
            "Tuesday/Thursday: Skill Development\n" +
            "- Balance exercises: 15 mins\n" +
            "- Coordination drills: 15 mins\n\n" +
            "Saturday: Family Activity\n" +
            "- Hiking: 60 mins\n" +
            "- Bike riding: 30 mins\n\n" +
            "Sunday: Creative Movement\n" +
            "- Dance: 30 mins\n" +
            "- Obstacle courses: 20 mins\n\n" +
            "Exercise Guidelines:\n" +
            "• Make it fun\n" +
            "• Variety is key\n" +
            "• Lead by example",
            "#035752"),

        new WorkoutPlanDetails("Elderly Nutrition", 
            "Daily: Gentle Movement\n" +
            "- Walking: 20-30 mins\n" +
            "- Chair exercises: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Light weights: 2 sets x 12 reps\n" +
            "- Resistance bands: 15 mins\n\n" +
            "Tuesday/Thursday: Balance\n" +
            "- Tai chi: 25 mins\n" +
            "- Standing exercises: 15 mins\n\n" +
            "Saturday: Social Activity\n" +
            "- Group exercise class\n" +
            "- Leisure swimming\n\n" +
            "Sunday: Recovery\n" +
            "- Stretching: 15 mins\n" +
            "- Relaxation: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Focus on safety\n" +
            "• Modify as needed\n" +
            "• Stay consistent",
            "#035752"),

        new WorkoutPlanDetails("Cancer Support", 
            "Daily: Gentle Movement\n" +
            "- Walking: 15-20 mins\n" +
            "- Deep breathing: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Light resistance: 2 sets x 10 reps\n" +
            "- Bodyweight exercises: 15 mins\n\n" +
            "Tuesday/Thursday: Flexibility\n" +
            "- Gentle yoga: 20 mins\n" +
            "- Stretching: 10 mins\n\n" +
            "Saturday: Leisure Activity\n" +
            "- Gardening: 30 mins\n" +
            "- Art therapy with movement\n\n" +
            "Sunday: Rest & Recovery\n" +
            "- Meditation: 15 mins\n" +
            "- Visualization: 10 mins\n\n" +
            "Important Notes:\n" +
            "• Consult oncology team\n" +
            "• Listen to your body\n" +
            "• Adjust based on treatment",
            "#035752"),

        new WorkoutPlanDetails("Liver Detox", 
            "Morning Routine:\n" +
            "- Dry brushing: 5 mins\n" +
            "- Yoga twists: 10 mins\n\n" +
            "Daily: Lymphatic Movement\n" +
            "- Rebounding: 15 mins\n" +
            "- Walking: 20 mins\n\n" +
            "Monday/Wednesday/Friday: Core Work\n" +
            "- Seated rotations: 3 sets x 12 reps\n" +
            "- Plank variations: 3 sets x 20 sec\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 25 mins\n" +
            "- Cycling: 15 mins\n\n" +
            "Weekend: Heat Therapy\n" +
            "- Sauna: 20 mins\n" +
            "- Contrast showers\n\n" +
            "Detox Tips:\n" +
            "• Stay hydrated\n" +
            "• Epsom salt baths\n" +
            "• Avoid alcohol",
            "#035752"),

        new WorkoutPlanDetails("Kidney Detox", 
            "Morning Routine:\n" +
            "- Kidney massage: 5 mins\n" +
            "- Yoga for kidneys: 10 mins\n\n" +
            "Daily: Gentle Movement\n" +
            "- Walking: 30 mins\n" +
            "- Seated exercises: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Light Strength\n" +
            "- Resistance bands: 2 sets x 12 reps\n" +
            "- Bodyweight exercises: 15 mins\n\n" +
            "Tuesday/Thursday: Water Exercise\n" +
            "- Swimming: 20 mins\n" +
            "- Aqua stretching: 10 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Meditation: 20 mins\n" +
            "- Breathing exercises: 10 mins\n\n" +
            "Detox Tips:\n" +
            "• Stay hydrated\n" +
            "• Avoid overexertion\n" +
            "• Monitor urine output",
            "#035752"),

        new WorkoutPlanDetails("Gut Detox", 
            "Morning Routine:\n" +
            "- Abdominal massage: 5 mins\n" +
            "- Yoga for digestion: 10 mins\n\n" +
            "Daily: Movement Breaks\n" +
            "- Walking after meals: 10 mins\n" +
            "- Seated twists: 5 mins every 2 hours\n\n" +
            "Monday/Wednesday/Friday: Core Work\n" +
            "- Pelvic tilts: 3 sets x 15 reps\n" +
            "- Cat/cow stretches: 3 sets x 10 reps\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Dancing: 20 mins\n\n" +
            "Weekend: Relaxation\n" +
            "- Yoga nidra: 20 mins\n" +
            "- Gentle stretching: 15 mins\n\n" +
            "Detox Tips:\n" +
            "• Stay hydrated\n" +
            "• Include fiber\n" +
            "• Probiotic foods",
            "#035752"),

        new WorkoutPlanDetails("Lung Health", 
            "Morning Routine:\n" +
            "- Deep breathing: 10 mins\n" +
            "- Postural exercises: 5 mins\n\n" +
            "Daily: Aerobic Exercise\n" +
            "- Walking: 30 mins\n" +
            "- Stair climbing: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Cardio\n" +
            "- Swimming: 25 mins\n" +
            "- Cycling: 20 mins\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Upper body focus: 3 sets x 12 reps\n" +
            "- Core exercises: 15 mins\n\n" +
            "Weekend: Outdoor Activity\n" +
            "- Hiking: 60 mins\n" +
            "- Fresh air walks\n\n" +
            "Breathing Tips:\n" +
            "• Practice diaphragmatic breathing\n" +
            "• Avoid polluted areas\n" +
            "• Stay hydrated",
            "#035752"),

        new WorkoutPlanDetails("Skin Health", 
            "Morning Routine:\n" +
            "- Dry brushing: 5 mins\n" +
            "- Sun salutations: 10 mins\n\n" +
            "Daily: Circulation Boosters\n" +
            "- Inversion poses: 3 mins\n" +
            "- Jump rope: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Dancing: 20 mins\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Full body workout: 3 sets x 12 reps\n" +
            "- Focus on compound movements\n\n" +
            "Weekend: Relaxation\n" +
            "- Yoga for skin health: 25 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Skin Tips:\n" +
            "• Stay hydrated\n" +
            "• Post-workout cleansing\n" +
            "• Antioxidant-rich foods",
            "#035752"),

        new WorkoutPlanDetails("Diabetic Foot Care", 
            "Daily: Circulation Focus\n" +
            "- Ankle circles: 3 sets x 10 reps\n" +
            "- Toe taps: 3 sets x 15 reps\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Seated leg exercises: 3 sets x 12 reps\n" +
            "- Resistance band work: 15 mins\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Swimming: 30 mins\n" +
            "- Stationary bike: 20 mins\n\n" +
            "Saturday: Balance Training\n" +
            "- Single-leg stands: 3 sets x 30 sec\n" +
            "- Heel-toe walking: 3 sets x 10 steps\n\n" +
            "Sunday: Foot Care\n" +
            "- Foot massage: 15 mins\n" +
            "- Inspection routine\n\n" +
            "Important Notes:\n" +
            "• Wear proper footwear\n" +
            "• Check feet daily\n" +
            "• Avoid high impact",
            "#035752"),

        new WorkoutPlanDetails("Throat Care", 
            "Morning Routine:\n" +
            "- Neck stretches: 5 mins\n" +
            "- Humming exercises: 5 mins\n\n" +
            "Daily: Postural Exercises\n" +
            "- Chin tucks: 3 sets x 10 reps\n" +
            "- Shoulder rolls: 3 sets x 12 reps\n\n" +
            "Monday/Wednesday/Friday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Nasal breathing focus\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Upper body workout: 3 sets x 12 reps\n" +
            "- Neck support exercises\n\n" +
            "Weekend: Relaxation\n" +
            "- Steam inhalation: 10 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Throat Tips:\n" +
            "• Stay hydrated\n" +
            "• Avoid mouth breathing\n" +
            "• Humidify air",
            "#035752"),

        new WorkoutPlanDetails("Brain Fog Relief", 
            "Morning Routine:\n" +
            "- Aerobic exercise: 20 mins\n" +
            "- Coordination drills: 10 mins\n\n" +
            "Daily: Movement Breaks\n" +
            "- Dance breaks: 5 mins every hour\n" +
            "- Standing desk work\n\n" +
            "Monday/Wednesday/Friday: HIIT\n" +
            "- Interval training: 20 mins\n" +
            "- Complex movements\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Compound lifts: 3 sets x 10 reps\n" +
            "- Unilateral exercises\n\n" +
            "Weekend: Novel Activities\n" +
            "- Learn new sport\n" +
            "- Brain games while moving\n\n" +
            "Cognitive Tips:\n" +
            "• Stay hydrated\n" +
            "• Omega-3 rich foods\n" +
            "• Quality sleep",
            "#035752"),

        new WorkoutPlanDetails("Anti-Aging", 
            "Morning Routine:\n" +
            "- Sun salutations: 10 mins\n" +
            "- Deep breathing: 5 mins\n\n" +
            "Daily: Movement Variety\n" +
            "- Walking: 30 mins\n" +
            "- Stretching: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Resistance training: 3 sets x 12 reps\n" +
            "- Focus on form\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Swimming: 30 mins\n" +
            "- Cycling: 20 mins\n\n" +
            "Weekend: Recovery\n" +
            "- Yoga: 25 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Longevity Tips:\n" +
            "• Stay active daily\n" +
            "• Include balance training\n" +
            "• Social activities",
            "#035752"),

        new WorkoutPlanDetails("Energy Boost", 
            "Morning Routine:\n" +
            "- Sunlight exposure: 10 mins\n" +
            "- Jumping jacks: 3 sets x 20 reps\n\n" +
            "Daily: Movement Breaks\n" +
            "- Stretching: 5 mins every hour\n" +
            "- Walking: 10 mins after meals\n\n" +
            "Monday/Wednesday/Friday: HIIT\n" +
            "- Circuit training: 20 mins\n" +
            "- Full body focus\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Compound lifts: 3 sets x 10 reps\n" +
            "- Power movements\n\n" +
            "Weekend: Outdoor Activity\n" +
            "- Hiking: 60 mins\n" +
            "- Sports: 30 mins\n\n" +
            "Energy Tips:\n" +
            "• Stay hydrated\n" +
            "• Balanced nutrition\n" +
            "• Quality sleep",
            "#035752"),

        new WorkoutPlanDetails("Cognitive Health", 
            "Morning Routine:\n" +
            "- Aerobic exercise: 20 mins\n" +
            "- Coordination drills: 10 mins\n\n" +
            "Daily: Novel Movements\n" +
            "- Learn new skill: 15 mins\n" +
            "- Dance breaks: 5 mins\n\n" +
            "Monday/Wednesday/Friday: Complex Training\n" +
            "- Multi-planar movements\n" +
            "- Balance challenges\n\n" +
            "Tuesday/Thursday: Strength\n" +
            "- Unilateral exercises: 3 sets x 10 reps\n" +
            "- Compound lifts\n\n" +
            "Weekend: Social Activity\n" +
            "- Group sports\n" +
            "- Dance classes\n\n" +
            "Brain Tips:\n" +
            "• Stay hydrated\n" +
            "• Omega-3 rich foods\n" +
            "• Mental challenges",
            "#035752"),

        new WorkoutPlanDetails("Inflammation Reduction", 
            "Morning Routine:\n" +
            "- Yoga: 15 mins\n" +
            "- Deep breathing: 5 mins\n\n" +
            "Daily: Gentle Movement\n" +
            "- Walking: 30 mins\n" +
            "- Stretching: 10 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Light resistance: 3 sets x 12 reps\n" +
            "- Focus on full range\n\n" +
            "Tuesday/Thursday: Water Exercise\n" +
            "- Swimming: 30 mins\n" +
            "- Aqua therapy: 15 mins\n\n" +
            "Weekend: Recovery\n" +
            "- Meditation: 20 mins\n" +
            "- Foam rolling: 15 mins\n\n" +
            "Anti-Inflammatory Tips:\n" +
            "• Stay hydrated\n" +
            "• Omega-3 rich foods\n" +
            "• Quality sleep",
            "#035752"),

        new WorkoutPlanDetails("Hormonal Balance", 
            "Morning Routine:\n" +
            "- Sunlight exposure: 10 mins\n" +
            "- Walking: 15 mins\n\n" +
            "Daily: Stress-Reducing Movement\n" +
            "- Stretching breaks: 5 mins\n" +
            "- Deep breathing: 3 mins\n\n" +
            "Monday/Wednesday/Friday: Strength\n" +
            "- Compound lifts: 3 sets x 10 reps\n" +
            "- Full body focus\n\n" +
            "Tuesday/Thursday: Cardio\n" +
            "- Brisk walking: 30 mins\n" +
            "- Dancing: 20 mins\n\n" +
            "Weekend: Yoga\n" +
            "- Hormone-balancing yoga: 30 mins\n" +
            "- Meditation: 15 mins\n\n" +
            "Balance Tips:\n" +
            "• Stay hydrated\n" +
            "• Regular schedule\n" +
            "• Avoid overtraining",
            "#035752"),

    };

    private void openDietDialog(DietPlanDetails dp) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(dp.name);
        dialog.setHeaderText(dp.name + " Plan");

        DialogPane dialogPane = dialog.getDialogPane();

        String base = dp.color;
        String dark = darkenColor(base, 0.6);
        dialogPane.setStyle(
                "-fx-background-radius: 15;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: linear-gradient(to bottom right, " + dark + ", " + base + ");"
        );

        VBox content = createPlanContent(dp);
        dialogPane.setContent(content);

        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: transparent;");
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setTextFill(Color.web("#F0EAD6"));
            headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        }

        dialogPane.getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);

        String okBase = "#F0EAD6";
        String okHover = "#D6C9A8";

        String okBaseStyle =
                "-fx-background-color: " + okBase + ";" +
                        "-fx-text-fill: " + dp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);";

        String okHoverStyle =
                "-fx-background-color: " + okHover + ";" +
                        "-fx-text-fill: " + dp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 3);";

        okButton.setStyle(okBaseStyle);
        okButton.setOnMouseEntered(ev -> okButton.setStyle(okHoverStyle));
        okButton.setOnMouseExited(ev -> okButton.setStyle(okBaseStyle));

        VBox.setMargin(okButton, new Insets(20, 0, 0, 0));

        dialog.showAndWait();
    }

    // --- WORKOUT PLANS PAGE ---

    private void openWorkoutPlanPage() {
        Stage stage = new Stage();
        stage.setTitle("Weekly Workout Plans");

        Label heading = new Label("Choose a Weekly Workout Plan");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#F0EAD6"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        // Use same icons as diet plan page for consistency
        String[] icons = {
                "⚖", "⬆", "💉", "❤", "💪",
                "🍃", "🥗", "🧂", "🩺", "💧",
                "💊", "🥬", "🍇", "🥚", "🍵",
                "🍏", "🧀", "🥕", "🧄", "🥔",
                "🌞", "🥛"
        };

        for (int i = 0; i < workoutPlans.length; i++) {
            WorkoutPlanDetails wp = workoutPlans[i];
            Button btn = new Button(icons[i % icons.length] + "  " + wp.name);
            btn.setPrefWidth(280);
            btn.setPrefHeight(50);
            btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            btn.setStyle("-fx-background-color: " + wp.color + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 15;" +
                    "-fx-cursor: hand;");

            btn.setOnAction(e -> showWorkoutDialog(wp));

            grid.add(btn, i % 2, i / 2);
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(820);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "  -fx-background: transparent;" +
                        "  -fx-background-color: transparent;" +
                        "  -fx-padding: 0;" +
                        "  -fx-control-inner-background: transparent;" +
                        "  -fx-background-insets: 0;" +
                        "  -fx-background-radius: 0;"
        );

        scrollPane.lookupAll(".scroll-bar").forEach(sb -> {
            sb.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pref-width: 6;" +
                            "-fx-padding: 0;"
            );
        });

        VBox root = new VBox(30, heading, scrollPane);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0A2F28;");
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    private void showWorkoutDialog(WorkoutPlanDetails wp) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(wp.name);
        dialog.setHeaderText(wp.name + " Plan");

        DialogPane dialogPane = dialog.getDialogPane();

        String base = wp.color;
        String dark = darkenColor(base, 0.6);
        dialogPane.setStyle(
                "-fx-background-radius: 15;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-color: linear-gradient(to bottom right, " + dark + ", " + base + ");"
        );

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);
        content.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 12;");

        Text workoutText = new Text(wp.planText);
        workoutText.setFont(Font.font("Segoe UI", 16));
        workoutText.setFill(Color.web("#DDD9CC"));
        workoutText.setWrappingWidth(440);

        content.getChildren().add(workoutText);
        dialogPane.setContent(content);

        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: transparent;");
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setTextFill(Color.web("#F0EAD6"));
            headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        }

        dialogPane.getButtonTypes().add(ButtonType.OK);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        String okBase = "#F0EAD6";
        String okHover = "#D6C9A8";

        String okBaseStyle =
                "-fx-background-color: " + okBase + ";" +
                        "-fx-text-fill: " + wp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 4, 0, 0, 2);";

        String okHoverStyle =
                "-fx-background-color: " + okHover + ";" +
                        "-fx-text-fill: " + wp.color + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 3);";

        okButton.setStyle(okBaseStyle);
        okButton.setOnMouseEntered(ev -> okButton.setStyle(okHoverStyle));
        okButton.setOnMouseExited(ev -> okButton.setStyle(okBaseStyle));

        VBox.setMargin(okButton, new Insets(20, 0, 0, 0));

        dialog.showAndWait();
    }

    // --- CONTACT US PAGE WITH FEEDBACK ---
    private void openContactUsPage() {
        Stage stage = new Stage();
        stage.setTitle("Contact Us");

        // Root container VBox - center aligned with maximum width for content
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40, 20, 40, 20));
        root.setBackground(new Background(new BackgroundFill(Color.web("#0C3B2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Card container - transparent background with white border and radius 5
        VBox contactCard = new VBox(30);
        contactCard.setPadding(new Insets(40));
        contactCard.setAlignment(Pos.TOP_CENTER);
        contactCard.setMaxWidth(600);
        contactCard.setPrefHeight(700);
        contactCard.setStyle(
                "-fx-border-radius: 5;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #FFFFFF;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);"
        );

        // Heading with large bold typography, white text
        Label line1 = new Label("");
        line1.setFont(Font.font("Poppins", 60));
        line1.setTextFill(Color.WHITE);

        Label line2 = new Label("Contact us");
        line2.setFont(Font.font("Poppins", 50));
        line2.setTextFill(Color.WHITE);

        Label line3 = new Label("");
        line3.setFont(Font.font("Poppins", 20));
        line3.setTextFill(Color.WHITE);

        VBox headingBox = new VBox(-10, line1, line2, line3);
        headingBox.setAlignment(Pos.CENTER);

        // Contact No label white
        Label callLabel = new Label("Call No: +920345678291");
        callLabel.setFont(Font.font("Poppins", 22));
        callLabel.setTextFill(Color.WHITE);
        callLabel.setAlignment(Pos.CENTER);

        // Email label white
        Label emailLabel = new Label("Email: nutrix@gmail.com");
        emailLabel.setFont(Font.font("Poppins", 22));
        emailLabel.setTextFill(Color.WHITE);
        emailLabel.setAlignment(Pos.CENTER);

        // Feedback button styled with black background and white text per guidelines (no change here)
        Button feedbackButton = new Button("Feedback");
        feedbackButton.setFont(Font.font("Poppins", 20));
        feedbackButton.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 50;" +
                        "-fx-cursor: hand;"
        );
        feedbackButton.setOnMouseEntered(e -> feedbackButton.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 50;" +
                        "-fx-cursor: hand;"
        ));
        feedbackButton.setOnMouseExited(e -> feedbackButton.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 50;" +
                        "-fx-cursor: hand;"
        ));
        feedbackButton.setOnAction(e -> openFeedbackWindow(stage));

        // Add all components to card
        contactCard.getChildren().addAll(headingBox, callLabel, emailLabel, feedbackButton);

        // Add card to root
        root.getChildren().add(contactCard);

        // Scene setup
        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    private void openFeedbackWindow(Stage owner) {
        Stage feedbackStage = new Stage();
        feedbackStage.initOwner(owner);
        feedbackStage.initModality(Modality.APPLICATION_MODAL);
        feedbackStage.setTitle("User Feedback");

        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        container.setAlignment(Pos.TOP_CENTER);
        container.setMaxWidth(600);

        Label titleLabel = new Label("User Feedback");
        titleLabel.setFont(Font.font("Poppins", 32));
        titleLabel.setTextFill(Color.web("#111827"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        TextArea feedbackDisplay = new TextArea();
        feedbackDisplay.setEditable(false);
        feedbackDisplay.setWrapText(true);
        feedbackDisplay.setStyle(
                "-fx-control-inner-background: #f9fafb;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-font-family: 'Poppins';" +
                        "-fx-font-size: 14px;"
        );
        feedbackDisplay.setPrefHeight(200);
        feedbackDisplay.setPrefWidth(560);

        String allFeedback = readAllFeedback();
        feedbackDisplay.setText(allFeedback);

        Label newFeedbackLabel = new Label("Write your feedback:");
        newFeedbackLabel.setFont(Font.font("Poppins", 18));
        newFeedbackLabel.setTextFill(Color.web("#6b7280"));

        TextArea newFeedbackArea = new TextArea();
        newFeedbackArea.setWrapText(true);
        newFeedbackArea.setPromptText("Enter your feedback here...");
        newFeedbackArea.setStyle(
                "-fx-control-inner-background: #f9fafb;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-font-family: 'Poppins';" +
                        "-fx-font-size: 14px;"
        );
        newFeedbackArea.setPrefHeight(120);
        newFeedbackArea.setPrefWidth(560);

        Button saveButton = new Button("Save Feedback");
        saveButton.setFont(Font.font("Poppins", 18));
        saveButton.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 50;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        saveButton.setOnMouseEntered(e -> saveButton.setStyle(
                "-fx-background-color: #374151;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 50;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(
                "-fx-background-color: #111827;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 50;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
        saveButton.setOnAction(e -> {
            String feedbackText = newFeedbackArea.getText().trim();
            if (feedbackText.isEmpty()) {
                showAlert("Warning", "Please write some feedback before saving.");
                return;
            }
            boolean saved = appendFeedback(feedbackText);
            if (saved) {
                String updatedFeedback = readAllFeedback();
                feedbackDisplay.setText(updatedFeedback);
                newFeedbackArea.clear();
                showAlert("Success", "Feedback saved successfully!");
            } else {
                showAlert("Error", "Failed to save feedback.");
            }
        });

        container.getChildren().addAll(titleLabel, feedbackDisplay, newFeedbackLabel, newFeedbackArea, saveButton);

        Scene feedbackScene = new Scene(container, 400, 500);
        feedbackStage.setScene(feedbackScene);
        feedbackStage.show();
    }

    private String readAllFeedback() {
        File file = new File("feedback.txt");
        if (!file.exists()) {
            return "";
        }
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("feedback.txt"));
            String content = new String(encoded, StandardCharsets.UTF_8);
            return content.trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private boolean appendFeedback(String feedback) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("feedback.txt", true))) {
            String formattedFeedback = String.format("%s%n---%n", feedback);
            writer.write(formattedFeedback);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

    // --- FRUIT & VEG BENEFITS PAGE ---
    static class FoodItem {
        String name;
        String benefits;
        String icon;
        String color;

        FoodItem(String name, String benefits, String icon, String color) {
            this.name = name;
            this.benefits = benefits;
            this.icon = icon;
            this.color = color;
        }
    }

    private final FoodItem[] fruits = new FoodItem[]{
            new FoodItem("Apple", "Rich in fiber and antioxidants. Supports heart health and may reduce risk of diabetes.", "🍎", "#27AE60"),
            new FoodItem("Banana", "High in potassium, supports heart health and blood pressure regulation. Good source of quick energy.", "🍌", "#27AE60"),
            new FoodItem("Orange", "Excellent source of vitamin C, boosts immunity and aids iron absorption. Contains flavonoids for heart health.", "🍊", "#27AE60"),
            new FoodItem("Blueberries", "Packed with antioxidants that may delay brain aging and improve memory. Supports urinary tract health.", "🫐", "#27AE60"),
            new FoodItem("Strawberries", "High in vitamin C, manganese, and antioxidants. May help regulate blood sugar levels.", "🍓", "#27AE60"),
            new FoodItem("Grapes", "Contain resveratrol for heart health. May protect against certain cancers and support eye health.", "🍇", "#27AE60"),
            new FoodItem("Pineapple", "Contains bromelain enzyme that aids digestion. Rich in vitamin C and manganese.", "🍍", "#27AE60"),
            new FoodItem("Mango", "High in vitamins A and C. Supports eye health, immunity, and contains digestive enzymes.", "🥭", "#27AE60"),
            new FoodItem("Kiwi", "Contains more vitamin C than oranges. Supports immune function and digestion with its fiber content.", "🥝", "#27AE60"),
            new FoodItem("Avocado", "Rich in healthy monounsaturated fats. Supports heart health and contains more potassium than bananas.", "🥑", "#27AE60"),
            new FoodItem("Watermelon", "Hydrating with high water content. Contains lycopene for heart health and citrulline for circulation.", "🍉", "#27AE60"),
            new FoodItem("Pomegranate", "Powerful antioxidants may reduce inflammation and lower blood pressure. Supports joint health.", "🍈", "#27AE60"),
            new FoodItem("Pear", "High in fiber for digestive health. Contains antioxidants and flavonoids that may reduce inflammation.", "🍐", "#27AE60"),
            new FoodItem("Peach", "Rich in vitamins A and C. Supports skin health and contains compounds that may reduce allergy symptoms.", "🍑", "#27AE60"),
            new FoodItem("Cherry", "Contains melatonin for better sleep. Anti-inflammatory properties may help with arthritis and gout.", "🍒", "#27AE60")
    };

    private final FoodItem[] vegetables = new FoodItem[]{
            new FoodItem("Spinach", "Rich in iron, calcium, and vitamins A, C, K. Supports bone health and may reduce oxidative stress.", "🥬", "#2ECC71"),
            new FoodItem("Broccoli", "High in fiber, vitamin C, and sulforaphane (anti-cancer compound). Supports detoxification.", "🥦", "#2ECC71"),
            new FoodItem("Carrot", "Excellent source of beta-carotene for eye health. Supports immune function and skin health.", "🥕", "#2ECC71"),
            new FoodItem("Tomato", "Contains lycopene for heart health. Rich in vitamin C, potassium, and folate.", "🍅", "#2ECC71"),
            new FoodItem("Bell Pepper", "Very high in vitamin C. Contains antioxidants that may protect against chronic diseases.", "🫑", "#2ECC71"),
            new FoodItem("Cucumber", "Hydrating with high water content. Contains antioxidants and supports skin health.", "🥒", "#2ECC71"),
            new FoodItem("Sweet Potato", "Rich in beta-carotene for eye health. High in fiber and supports blood sugar regulation.", "🍠", "#2ECC71"),
            new FoodItem("Garlic", "Contains allicin with potent medicinal properties. May boost immune function and lower blood pressure.", "🧄", "#2ECC71"),
            new FoodItem("Onion", "Rich in antioxidants and compounds with anti-inflammatory effects. May support heart health.", "🧅", "#2ECC71"),
            new FoodItem("Kale", "One of the most nutrient-dense foods. High in vitamins A, K, C, and minerals like calcium.", "🥬", "#2ECC71"),
            new FoodItem("Cauliflower", "High in fiber and antioxidants. Contains choline for brain health and supports detoxification.", "🥦", "#2ECC71"),
            new FoodItem("Brussels Sprouts", "Rich in vitamin K and antioxidants. Contains compounds that may protect against cancer.", "🥬", "#2ECC71"),
            new FoodItem("Zucchini", "Low in calories but high in nutrients. Contains antioxidants and supports healthy digestion.", "🥒", "#2ECC71"),
            new FoodItem("Eggplant", "Contains nasunin (a potent antioxidant). May support heart health and blood sugar control.", "🍆", "#2ECC71"),
            new FoodItem("Asparagus", "Excellent source of folate. Contains prebiotic fiber that supports gut bacteria.", "🌱", "#2ECC71")
    };

    private VBox createFoodCard(FoodItem item) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(360);
        card.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.55);" +
                        "-fx-background-radius: 12;"
        );

        Text nameText = new Text(item.icon + "  " + item.name);
        nameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        nameText.setFill(Color.web("#F0EAD6"));

        Text benefitsText = new Text(item.benefits);
        benefitsText.setFont(Font.font("Segoe UI", 16));
        benefitsText.setFill(Color.web("#DDD9CC"));
        benefitsText.setWrappingWidth(320);

        card.getChildren().addAll(nameText, benefitsText);

        // Set background color lightly tinted
        card.setBackground(new Background(new BackgroundFill(
                Color.web(item.color).deriveColor(0, 1, 1, 0.2),
                new CornerRadii(12),
                Insets.EMPTY
        )));

        // Add white border
        card.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(2),
                Insets.EMPTY
        )));

        return card;
    }

    private void openFruitVeggiePage() {
        Stage stage = new Stage();
        stage.setTitle("Fruit & Vegetable Benefits");

        Label heading = new Label("Fruit & Vegetable Benefits");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#F0EAD6"));

        Label fruitsHeading = new Label("Fruits");
        fruitsHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        fruitsHeading.setTextFill(Color.web("#F0EAD6"));

        GridPane fruitsGrid = new GridPane();
        fruitsGrid.setHgap(20);
        fruitsGrid.setVgap(20);
        fruitsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < fruits.length; i++) {
            VBox card = createFoodCard(fruits[i]);
            fruitsGrid.add(card, i % 2, i / 2);
        }

        Label veggiesHeading = new Label("Vegetables");
        veggiesHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        veggiesHeading.setTextFill(Color.web("#F0EAD6"));

        GridPane veggiesGrid = new GridPane();
        veggiesGrid.setHgap(20);
        veggiesGrid.setVgap(20);
        veggiesGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < vegetables.length; i++) {
            VBox card = createFoodCard(vegetables[i]);
            veggiesGrid.add(card, i % 2, i / 2);
        }

        VBox fruitsSection = new VBox(15, fruitsHeading, fruitsGrid);
        fruitsSection.setAlignment(Pos.CENTER);

        VBox veggiesSection = new VBox(15, veggiesHeading, veggiesGrid);
        veggiesSection.setAlignment(Pos.CENTER);

        VBox content = new VBox(30, fruitsSection, veggiesSection);
        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(820);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "  -fx-background: transparent;" +
                        "  -fx-background-color: transparent;" +
                        "  -fx-padding: 0;" +
                        "  -fx-control-inner-background: transparent;" +
                        "  -fx-background-insets: 0;" +
                        "  -fx-background-radius: 0;"
        );

        scrollPane.lookupAll(".scroll-bar").forEach(sb -> {
            sb.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pref-width: 6;" +
                            "-fx-padding: 0;"
            );
        });

        VBox root = new VBox(30, heading, scrollPane);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0A2F28;");
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    // --- HEALTHY LIFESTYLE TIPS PAGE ---
    static class TipItem {
        String category;
        String tip;
        String icon;
        String color;

        TipItem(String category, String tip, String icon, String color) {
            this.category = category;
            this.tip = tip;
            this.icon = icon;
            this.color = color;
        }
    }

    private final TipItem[] healthTips = new TipItem[]{
            new TipItem("Nutrition", "Start your day with warm lemon water to kickstart digestion", "🍋", "#27AE60"),
            new TipItem("Nutrition", "Include protein in every meal to maintain muscle mass", "🥚", "#27AE60"),
            new TipItem("Nutrition", "Stay hydrated - drink at least 8 glasses of water daily", "💧", "#27AE60"),
            new TipItem("Nutrition", "Choose whole grains over refined carbohydrates", "🌾", "#27AE60"),
            new TipItem("Nutrition", "Eat slowly and mindfully to improve digestion", "🧘", "#27AE60"),
            new TipItem("Exercise", "Take a 5-minute walk after meals for better digestion", "🚶", "#2ECC71"),
            new TipItem("Exercise", "Incorporate strength training 2-3 times per week", "💪", "#2ECC71"),
            new TipItem("Exercise", "Stretch daily to improve flexibility and circulation", "🧘‍♂️", "#2ECC71"),
            new TipItem("Exercise", "Stand up and move for 5 minutes every hour", "🔄", "#2ECC71"),
            new TipItem("Exercise", "Aim for 10,000 steps daily for cardiovascular health", "👣", "#2ECC71"),
            new TipItem("Sleep", "Maintain consistent sleep and wake times", "⏰", "#16A085"),
            new TipItem("Sleep", "Create a relaxing bedtime routine", "🛁", "#16A085"),
            new TipItem("Sleep", "Keep your bedroom cool (60-67°F) for optimal sleep", "❄️", "#16A085"),
            new TipItem("Sleep", "Avoid screens 1 hour before bedtime", "📵", "#16A085"),
            new TipItem("Sleep", "Limit caffeine after 2pm for better sleep quality", "☕", "#16A085"),
            new TipItem("Mental Health", "Practice gratitude journaling daily", "📔", "#1ABC9C"),
            new TipItem("Mental Health", "Take deep breaths when stressed (4-7-8 technique)", "🌬️", "#1ABC9C"),
            new TipItem("Mental Health", "Spend time in nature to reduce stress", "🌳", "#1ABC9C"),
            new TipItem("Mental Health", "Limit social media to 30 minutes daily", "📱", "#1ABC9C"),
            new TipItem("Mental Health", "Connect with loved ones regularly", "👪", "#1ABC9C"),
            new TipItem("Wellness", "Get 15 minutes of sunlight daily for vitamin D", "☀️", "#1D8348"),
            new TipItem("Wellness", "Practice good posture to prevent back pain", "🧍", "#1D8348"),
            new TipItem("Wellness", "Wash hands frequently to prevent illness", "🧼", "#1D8348"),
            new TipItem("Wellness", "Schedule regular health check-ups", "🏥", "#1D8348"),
            new TipItem("Wellness", "Laugh daily - it boosts immunity and mood", "😂", "#1D8348"),
            new TipItem("Habits", "Meal prep on weekends for healthier weekdays", "🍱", "#7F8C8D"),
            new TipItem("Habits", "Keep healthy snacks visible and accessible", "🍎", "#7F8C8D"),
            new TipItem("Habits", "Floss daily for oral and heart health", "🦷", "#7F8C8D"),
            new TipItem("Habits", "Take stairs instead of elevators when possible", "🪜", "#7F8C8D"),
            new TipItem("Habits", "Practice the 20-20-20 rule for eye health", "👀", "#7F8C8D")
    };

    private VBox createTipCard(TipItem tip) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.TOP_LEFT);
        card.setMaxWidth(380);
        card.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.55);" +
                        "-fx-background-radius: 12;"
        );

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Text iconText = new Text(tip.icon);
        iconText.setFont(Font.font(24));
        iconText.setFill(Color.WHITE);

        Text categoryText = new Text(tip.category);
        categoryText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        categoryText.setFill(Color.web("#F0EAD6"));

        header.getChildren().addAll(iconText, categoryText);

        Text tipText = new Text(tip.tip);
        tipText.setFont(Font.font("Segoe UI", 16));
        tipText.setFill(Color.web("#DDD9CC"));
        tipText.setWrappingWidth(340);

        card.getChildren().addAll(header, tipText);

        card.setBorder(new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(2),
                Insets.EMPTY
        )));

        return card;
    }

    private void openHealthyTipsPage() {
        Stage stage = new Stage();
        stage.setTitle("30 Healthy Lifestyle Tips");

        Label heading = new Label("Healthy Lifestyle Tips");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        heading.setTextFill(Color.web("#F0EAD6"));

        GridPane tipsGrid = new GridPane();
        tipsGrid.setHgap(20);
        tipsGrid.setVgap(20);
        tipsGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < healthTips.length; i++) {
            VBox card = createTipCard(healthTips[i]);
            tipsGrid.add(card, i % 2, i / 2);
        }

        ScrollPane scrollPane = new ScrollPane(tipsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(820);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;" +
                        "-fx-padding: 0;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 0;"
        );

        scrollPane.lookupAll(".scroll-bar").forEach(sb -> {
            sb.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-pref-width: 6;" +
                            "-fx-padding: 0;"
            );
        });

        VBox root = new VBox(30, heading, scrollPane);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0A2F28;");
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    // --- BMI CALCULATOR PAGE ---

    private void openBMICalculator() {
        Stage stage = new Stage();
        stage.setTitle("BMI Calculator");

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.web("#0C3B2E"), CornerRadii.EMPTY, Insets.EMPTY)));

        VBox card = new VBox(30);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(600);
        card.setStyle(
                "-fx-background-color: " + toHex(Color.web("#0C3B2E")) + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);"
        );

        Label title = new Label("BMI Calculator");
        title.setFont(Font.font("Poppins", 36));
        title.setTextFill(Color.WHITE);
        title.setAlignment(Pos.CENTER);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(20);
        inputGrid.setVgap(20);
        inputGrid.setAlignment(Pos.CENTER);

        Label heightLabel = new Label("Height (cm):");
        heightLabel.setFont(Font.font("Poppins", 18));
        heightLabel.setTextFill(Color.WHITE);

        TextField heightField = createStyledTextField();

        Label weightLabel = new Label("Weight (kg):");
        weightLabel.setFont(Font.font("Poppins", 18));
        weightLabel.setTextFill(Color.WHITE);

        TextField weightField = createStyledTextField();

        inputGrid.add(heightLabel, 0, 0);
        inputGrid.add(heightField, 1, 0);
        inputGrid.add(weightLabel, 0, 1);
        inputGrid.add(weightField, 1, 1);

        Button calculateBtn = new Button("Click");
        styleWhiteButtonWithDarkGreenText(calculateBtn);

        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("Poppins", 24));
        resultLabel.setTextFill(Color.WHITE);
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(500);
        resultLabel.setAlignment(Pos.CENTER);

        calculateBtn.setOnAction(e -> {
            String heightText = heightField.getText().trim();
            String weightText = weightField.getText().trim();
            try {
                if (heightText.isEmpty() || weightText.isEmpty()) {
                    throw new IllegalArgumentException("Please enter both height and weight.");
                }

                double heightCm = Double.parseDouble(heightText);
                double weightKg = Double.parseDouble(weightText);

                if (heightCm < 50 || heightCm > 300) {
                    showAlert("Invalid Input", "Please enter a realistic height between 50 cm and 300 cm");
                    return;
                }
                if (weightKg < 10 || weightKg > 500) {
                    showAlert("Invalid Input", "Please enter a realistic weight between 10 kg and 500 kg");
                    return;
                }

                double heightMeters = heightCm / 100.0;
                double bmi = weightKg / (heightMeters * heightMeters);
                String category = getBMICategory(bmi);

                String resultText = String.format("Your BMI is %.2f\nCategory: %s", bmi, category);
                resultLabel.setText(resultText);
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numeric values for height and weight.");
            } catch (IllegalArgumentException ex) {
                showAlert("Input Error", ex.getMessage());
            }
        });

        card.getChildren().addAll(title, inputGrid, calculateBtn, resultLabel);
        root.getChildren().add(card);

        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    private TextField createStyledTextField() {
        TextField field = new TextField();
        field.setPrefWidth(200);
        field.setFont(Font.font("Poppins", 16));
        field.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
        );
        return field;
    }

    private void styleWhiteButtonWithDarkGreenText(Button button) {
        String bgColorHex = toHex(Color.WHITE);
        String textColorHex = toHex(Color.web("#0C3B2E"));
        button.setFont(Font.font("Poppins", 18));
        button.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + textColorHex + ";" +
                        "-fx-padding: 10 25;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: " + textColorHex + ";" +
                        "-fx-border-width: 1.5;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + textColorHex + ";" +
                        "-fx-padding: 10 25;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: " + textColorHex + ";" +
                        "-fx-border-width: 2;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + textColorHex + ";" +
                        "-fx-padding: 10 25;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-color: " + textColorHex + ";" +
                        "-fx-border-width: 1.5;"
        ));
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal weight";
        else if (bmi < 30) return "Overweight";
        else return "Obesity";
    }

    // --- CALORIE TRACKER PAGE ---

    private final Color CALTR_BG_COLOR = Color.web("#0C3B2E");
    private int totalCalories = 0;
    private final String CALTR_DATA_FILE = "calorie_data.txt";
    private LocalDate caltrCurrentDate = LocalDate.now();
    private final List<String> foodEntries = new ArrayList<>();

    private void openCalorieTracker() {
        Stage stage = new Stage();
        // Load saved data for current date
        loadCalorieData();

        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));
        root.setBackground(new Background(new BackgroundFill(CALTR_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox card = new VBox(30);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.TOP_CENTER);
        card.setMaxWidth(600);
        card.setStyle(
                "-fx-background-color: " + toHex(CALTR_BG_COLOR) + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 3;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);"
        );

        Label dateLabel = new Label(caltrCurrentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setFont(Font.font("Poppins", 20));
        dateLabel.setTextFill(Color.WHITE);

        Label title = new Label("Calorie Tracker");
        title.setFont(Font.font("Poppins", 36));
        title.setTextFill(Color.WHITE);

        Label totalLabel = new Label("Total Calories: " + totalCalories);
        totalLabel.setFont(Font.font("Poppins", 24));
        totalLabel.setTextFill(Color.WHITE);

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(20);
        inputGrid.setVgap(20);
        inputGrid.setAlignment(Pos.CENTER);

        Label foodLabel = new Label("Food Item:");
        foodLabel.setFont(Font.font("Poppins", 18));
        foodLabel.setTextFill(Color.WHITE);
        TextField foodField = createStyledTextField();

        Label caloriesLabel = new Label("Calories:");
        caloriesLabel.setFont(Font.font("Poppins", 18));
        caloriesLabel.setTextFill(Color.WHITE);
        TextField caloriesField = createStyledTextField();

        Label mealLabel = new Label("Meal Type:");
        mealLabel.setFont(Font.font("Poppins", 18));
        mealLabel.setTextFill(Color.WHITE);
        ComboBox<String> mealCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Breakfast", "Lunch", "Dinner", "Snack"
        ));
        mealCombo.setValue("Breakfast");
        mealCombo.setPrefWidth(200);
        mealCombo.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 16px; -fx-text-fill: white;");

        inputGrid.addRow(0, foodLabel, foodField);
        inputGrid.addRow(1, caloriesLabel, caloriesField);
        inputGrid.addRow(2, mealLabel, mealCombo);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Add Food");
        styleButton(addBtn, Color.WHITE, CALTR_BG_COLOR);

        Button resetBtn = new Button("Reset Day");
        styleButton(resetBtn, Color.WHITE, CALTR_BG_COLOR);

        HBox dateNavBox = new HBox(10);
        dateNavBox.setAlignment(Pos.CENTER);
        Button prevDayBtn = new Button("<");
        Button nextDayBtn = new Button(">");
        styleButton(prevDayBtn, Color.WHITE, CALTR_BG_COLOR);
        styleButton(nextDayBtn, Color.WHITE, CALTR_BG_COLOR);
        prevDayBtn.setPrefWidth(40);
        nextDayBtn.setPrefWidth(40);
        dateNavBox.getChildren().addAll(prevDayBtn, dateLabel, nextDayBtn);

        buttonBox.getChildren().addAll(addBtn, resetBtn);

        Label logTitle = new Label("Food Log");
        logTitle.setFont(Font.font("Poppins", 22));
        logTitle.setTextFill(Color.WHITE);

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(200);
        logArea.setPrefWidth(500);
        logArea.setStyle(
                "-fx-control-inner-background: rgba(0,0,0,0.25);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Poppins';" +
                        "-fx-font-size: 14px;"
        );

        refreshCalorieLog(logArea, totalLabel);

        addBtn.setOnAction(e -> {
            addFoodEntry(foodField, caloriesField, mealCombo, totalLabel, logArea);
            saveCalorieData();
        });

        resetBtn.setOnAction(e -> {
            resetCalorieTracker(totalLabel, logArea);
            saveCalorieData();
        });

        prevDayBtn.setOnAction(e -> {
            caltrCurrentDate = caltrCurrentDate.minusDays(1);
            dateLabel.setText(caltrCurrentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadCalorieData();
            refreshCalorieLog(logArea, totalLabel);
        });

        nextDayBtn.setOnAction(e -> {
            caltrCurrentDate = caltrCurrentDate.plusDays(1);
            dateLabel.setText(caltrCurrentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadCalorieData();
            refreshCalorieLog(logArea, totalLabel);
        });

        card.getChildren().addAll(dateNavBox, title, totalLabel, inputGrid, buttonBox, logTitle, logArea);
        root.getChildren().add(card);

        stage.setScene(new Scene(root, 1380, 700));
        stage.show();
    }

    private void refreshCalorieLog(TextArea logArea, Label totalLabel) {
        logArea.clear();
        totalCalories = 0;

        for (String entry : foodEntries) {
            logArea.appendText(entry + "\n");
            String[] parts = entry.split(": ");
            if (parts.length > 1) {
                try {
                    String caloriePart = parts[parts.length - 1].replace(" calories", "");
                    totalCalories += Integer.parseInt(caloriePart);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing calories from entry: " + entry);
                }
            }
        }

        totalLabel.setText("Total Calories: " + totalCalories);
    }

    private void loadCalorieData() {
        foodEntries.clear();
        totalCalories = 0;

        String dateStr = caltrCurrentDate.format(DateTimeFormatter.ISO_DATE);
        boolean foundDate = false;

        try {
            if (Files.exists(Paths.get(CALTR_DATA_FILE))) {
                List<String> allLines = Files.readAllLines(Paths.get(CALTR_DATA_FILE));

                for (String line : allLines) {
                    if (line.startsWith("DATE:")) {
                        foundDate = line.substring(5).equals(dateStr);
                    } else if (foundDate && !line.trim().isEmpty()) {
                        foodEntries.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data file: " + e.getMessage());
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    private void saveCalorieData() {
        String dateStr = caltrCurrentDate.format(DateTimeFormatter.ISO_DATE);
        List<String> allLines = new ArrayList<>();
        boolean dateExists = false;

        try {
            if (Files.exists(Paths.get(CALTR_DATA_FILE))) {
                allLines = Files.readAllLines(Paths.get(CALTR_DATA_FILE));
            }

            List<String> newLines = new ArrayList<>();
            boolean currentSection = false;

            for (String line : allLines) {
                if (line.startsWith("DATE:")) {
                    if (line.substring(5).equals(dateStr)) {
                        currentSection = true;
                        dateExists = true;
                        newLines.add(line);
                        newLines.addAll(foodEntries);
                    } else {
                        currentSection = false;
                        newLines.add(line);
                    }
                } else if (!currentSection) {
                    newLines.add(line);
                }
            }

            if (!dateExists) {
                newLines.add("DATE:" + dateStr);
                newLines.addAll(foodEntries);
            }

            Files.write(Paths.get(CALTR_DATA_FILE), newLines);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            showAlert("Error", "Failed to save data: " + e.getMessage());
        }
    }

    private void addFoodEntry(TextField foodField, TextField caloriesField,
                              ComboBox<String> mealCombo, Label totalLabel,
                              TextArea logArea) {
        try {
            String food = foodField.getText().trim();
            int calories = Integer.parseInt(caloriesField.getText().trim());
            String meal = mealCombo.getValue();

            if (food.isEmpty() || calories <= 0) {
                throw new IllegalArgumentException("Invalid input");
            }

            String entry = String.format("[%s] %s: %d calories", meal, food, calories);
            foodEntries.add(entry);
            refreshCalorieLog(logArea, totalLabel);

            foodField.clear();
            caloriesField.clear();
            foodField.requestFocus();
        } catch (Exception ex) {
            showAlert("Error", "Please enter valid food and calorie values");
        }
    }

    private void resetCalorieTracker(Label totalLabel, TextArea logArea) {
        totalCalories = 0;
        foodEntries.clear();
        refreshCalorieLog(logArea, totalLabel);
    }







    private void styleButton(Button button, Color textColor, Color bgColor) {
        String txtColorHex = toHex(textColor);
        String bgColorHex = toHex(bgColor);
        button.setFont(Font.font("Poppins", 18));
        button.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + txtColorHex + ";" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + bgColor.brighter().toString().replace("0x", "#") + ";" +
                        "-fx-text-fill: " + txtColorHex + ";" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + bgColorHex + ";" +
                        "-fx-text-fill: " + txtColorHex + ";" +
                        "-fx-padding: 10 20;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));
    }

    // --- ABOUT US PAGE ---

    private void openAboutUsPage() {
        Stage stage = new Stage();
        stage.setTitle("About Nutrix");

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.web("#0C3B2E"), CornerRadii.EMPTY, Insets.EMPTY)));
        root.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setSpacing(20);
        mainContent.setPadding(new Insets(20));

        VBox aboutCard = new VBox(20);
        aboutCard.setPadding(new Insets(30));
        aboutCard.setAlignment(Pos.TOP_CENTER);
        aboutCard.setMaxWidth(900);
        aboutCard.setMinWidth(600);
        aboutCard.setStyle(
                "-fx-background-color: #0C3B2E;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );

        Label heading = new Label("About Nutrix");
        heading.setFont(Font.font("Poppins", 36));
        heading.setTextFill(Color.WHITE);
        heading.setStyle("-fx-font-weight: bold;");

        Label subheading = new Label("Your Personalized Nutrition Companion");
        subheading.setFont(Font.font("Poppins", 24));
        subheading.setTextFill(Color.WHITE.deriveColor(1, 1, 1, 0.9));

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: white;");
        divider.setMaxWidth(600);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20, 40, 20, 40));

        // Add sections as per original content

        addSection(contentBox, "Who We Are",
                "Nutrix is a revolutionary nutrition platform  with a vision to transform " +
                        "how people approach their health. We combine cutting-edge technology with evidence-based " +
                        "nutrition science to create personalized wellness solutions for individuals worldwide.");

        addSection(contentBox, "Our Mission",
                "To empower individuals to take control of their health through personalized nutrition, " +
                        "making expert guidance accessible to everyone regardless of location or budget. We believe " +
                        "that proper nutrition is the foundation of a healthy, fulfilling life.");

        addSection(contentBox, "Our Services",
                "• AI-powered personalized meal plans\n" +
                        "• Comprehensive nutrition tracking and analysis\n" +
                        "• One-on-one consultations with certified dietitians\n" +
                        "• Educational resources and workshops\n" +
                        "• Community support and wellness challenges\n" +
                        "• Mobile app for on-the-go tracking");

        addSection(contentBox, "Our Team",
                "Nutrix was founded by a team of nutrition scientists, software engineers, and healthcare " +
                        "professionals passionate about bridging the gap between nutrition science and everyday life. " +
                        "Our team includes:\n\n" +
                        "• Certified Nutritionists and Dietitians\n" +
                        "• Software Developers and Data Scientists\n" +
                        "• Medical Doctors and Researchers\n" +
                        "• Wellness Coaches and Fitness Experts");

        addSection(contentBox, "Our Values",
                "• Science-Based Approach: All recommendations grounded in peer-reviewed research\n" +
                        "• Personalization: Tailored solutions for each individual's unique needs\n" +
                        "• Accessibility: Breaking down barriers to quality nutrition advice\n" +
                        "• Integrity: Transparent and ethical in all our practices\n" +
                        "• Innovation: Continuously improving our platform and services");

        aboutCard.getChildren().addAll(heading, subheading, divider, contentBox);
        mainContent.getChildren().add(aboutCard);
        scrollPane.setContent(mainContent);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void addSection(VBox container, String title, String content) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Poppins", 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");

        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Poppins", 16));
        contentLabel.setTextFill(Color.WHITE);
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(6);

        VBox section = new VBox(5, titleLabel, contentLabel);
        section.setPadding(new Insets(0, 0, 15, 0));
        container.getChildren().add(section);
    }

    // ====================== UTILITY METHODS ======================

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

}

