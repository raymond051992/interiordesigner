package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.AppScreens;
import com.ggwp.interiordesigner.object.EmptyRoomSelector;
import com.ggwp.interiordesigner.object.catalog.ObjectCatalog;
import com.ggwp.utils.ToolUtils;

import javax.tools.Tool;

public class MenuScreen extends AppScreen{

    private Skin skin;
    private Stage stage;
    private Texture backgroundImage;
    private Texture gradient;

    private Table newDesignOption;
    final TextButton createNewBtn;
    final TextButton catalogBtn;
    final TextButton exitBtn;

    BitmapFont titleFont;
    BitmapFont textFont;

    private final AppScreen thisAppScreen;

    public MenuScreen(){
        thisAppScreen = this;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createFonts();

        skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 20, Pixmap.Format.RGBA8888);

        skin.add("defaultButton", new Texture(pixmap));

        pixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.5f));
        pixmap.fill();

        skin.add("defaultButtonHover", new Texture(pixmap));

        Pixmap pixmap2 = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap2.setColor(Color.rgba8888(1f, 1f, 1f, 0.3f));

        pixmap2.fill();
        skin.add("defaultButtonHover2", new Texture(pixmap2));

        skin.add("defaultFont", textFont);
        skin.add("defaultTitleFont", titleFont);

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = skin.getFont("defaultFont");
        skin.add("default", style, Window.WindowStyle.class);

        Label.LabelStyle  style1 = new Label.LabelStyle();
        style1.font = skin.getFont("defaultFont");
        skin.add("default",style1,Label.LabelStyle.class);

        createNewBtn = new TextButton("CREATE NEW DESIGN", createButtonStyle(Color.valueOf("#2ecc71")));
        createNewBtn.pad(10);
        catalogBtn = new TextButton("CATALOG", createButtonStyle(Color.valueOf("#f1c40f")));
        catalogBtn.pad(10);

        exitBtn = new TextButton("EXIT", createButtonStyle(Color.valueOf("#1abc9c")));
        exitBtn.pad(10);

        Table table = new Table();

        table.row().pad(5);
        table.add(createNewBtn).align(Align.left);
        table.row().pad(5);
        table.add(catalogBtn).align(Align.left);
        table.row().pad(5);
        table.add(exitBtn).align(Align.left);

        Container wrapper = new Container<Table>(table);
        wrapper.setPosition(200, 120);
        stage.addActor(wrapper);

        loadCreateNewOptions();

        backgroundImage = new Texture("menu2.jpg");
        gradient = new Texture("gradient-black.png");
        addListeners();

        loadObjects();
    }

    private void loadCreateNewOptions(){
        TextButton.TextButtonStyle style5 = new TextButton.TextButtonStyle();
        style5.up = skin.getDrawable("defaultButtonHover2");
        style5.font = skin.getFont("defaultFont");
        style5.fontColor = Color.WHITE;

        TextButton takePictureBtn = new TextButton("TAKE A PICTURE", style5);
        takePictureBtn .pad(10);
        TextButton fromGallryBtn = new TextButton("FROM GALLERY", style5);
        fromGallryBtn.pad(10);
        TextButton emptyRoomBtn = new TextButton("EMPTY ROOM", style5);
        emptyRoomBtn.pad(10);

        newDesignOption = new Table(skin);
        newDesignOption.setFillParent(true);

        newDesignOption.background(new Image(new Texture("gradient2.png")).getDrawable());

        newDesignOption.add(takePictureBtn).padRight(5).width(150);
        newDesignOption.add(fromGallryBtn).padRight(5).width(150);
        newDesignOption.add(emptyRoomBtn).width(150);

        takePictureBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent il, float x, float y) {
                openDeviceCamera();
            }
        });

        fromGallryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent il, float x, float y) {
                FileHandle fileHandle = Gdx.files.internal("Rooms/room2.jpg");
                Main.getInstance().setScreen(new RoomSetupScreen(fileHandle, false));
//                openDeviceGallery();
            }
        });


        emptyRoomBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    final EmptyRoomSelector c = EmptyRoomSelector.construct(stage);


                    ToolUtils.removeScreenInputProcessor(stage);
                    stage.addActor(c);


            }
        });
    }

    public static void openDeviceCamera() {
        Object[][] directory = {{"savedirectory", Main.screenTemplateSaveDirectory}};
        Main.getInstance().setScreen(new ImageSelectionScreen());
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.IMAGE_CAPTURE,
                ToolUtils.createMapFromList(directory));
    }

    public static void openDeviceGallery() {
        Main.getInstance().setScreen(new ImageSelectionScreen());
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.GET_IMAGE_FROM_GALLERY,
                null);
    }

    private void setDisableMenuButton(boolean disable){
        createNewBtn.setDisabled(disable);
        catalogBtn.setDisabled(disable);
        exitBtn .setDisabled(disable);

    }

    private void loadObjects(){
        ObjectCatalog.init("Catalog", skin);
    }

    private void addListeners(){
        createNewBtn.addListener(new ClickListener(){
            @Override
            public void clicked (InputEvent il,float x,float y) {
                stage.addActor(newDesignOption);
                setDisableMenuButton(true);
            }
        });
    }

    private TextButton.TextButtonStyle createButtonStyle(Color c){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("defaultButtonHover");
        textButtonStyle.font = skin.getFont("defaultTitleFont");
        textButtonStyle.fontColor = c;
        return textButtonStyle;
    }

    private void createFonts() {
        FileHandle fontFile = Gdx.files.internal("data/Bernardo-Moda-Bold.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 14;
        textFont = generator.generateFont(parameter);
        parameter.size = 28;
        titleFont = generator.generateFont(parameter);
        generator.dispose();
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.getBatch().draw(gradient, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
