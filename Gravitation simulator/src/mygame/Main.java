package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import static com.jme3.math.Vector3f.ZERO;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author Matías Bonino
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private BulletAppState bulletAppState;

    /**
     * Prepare HUD.
     */
    BitmapText hudText;
    private long t = 0;
    private int quantity = 0;

    private RigidBodyControl ball_phy;
    private static Sphere sphere;

    static {
        /**
         * Initialize the cannon ball geometry
         */
        sphere = new Sphere(32, 32, 0.4f, true, false);
        sphere.setTextureMode(TextureMode.Projected);
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics Game
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        flyCam.setMoveSpeed(30 * speed);

        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "shoot");

        Geometry ball_geo = new Geometry("attractor", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        ball_geo.setMaterial(mat);
        rootNode.attachChild(ball_geo);
        ball_geo.setLocalTranslation(Vector3f.UNIT_X.mult(3));

        ball_phy = new RigidBodyControl(1f);
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        //bulletAppState.physicsTick(bulletAppState.getPhysicsSpace(), t);
        ball_phy.setGravity(ZERO);
        ball_phy.setLinearVelocity(Vector3f.UNIT_Y.mult(8));

        Box b = new Box(1, 1, 1);
        Geometry b_geom = new Geometry("Box", b);
        Material b_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        b_mat.setColor("Color", ColorRGBA.Blue);
        b_geom.setMaterial(b_mat);
        rootNode.attachChild(b_geom);
    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                makeCannonBall();
            }
        }
    };

    public void makeCannonBall() {

        Geometry ball_geo = new Geometry("cannon ball", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        ball_geo.setMaterial(mat);
        rootNode.attachChild(ball_geo);

        ball_geo.setLocalTranslation(cam.getLocation());
        ball_phy = new RigidBodyControl(1f);

        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        ball_phy.setGravity(ZERO);

        quantity++;

        ball_phy.setLinearVelocity(cam.getDirection().mult(5f));
    }

    @Override
    public void simpleUpdate(float tpf) {

        for (PhysicsRigidBody rbc : bulletAppState.getPhysicsSpace().getRigidBodyList()) {

            rbc.applyCentralForce(rbc.getPhysicsLocation().normalize().mult(-200f * rbc.getMass()/ rbc.getPhysicsLocation().lengthSquared()/*.distanceSquared(ZERO)*/));

            if (t % 60 != 0) {
                if (hudText != null) {
                    guiNode.detachChild(hudText);
                }
                hudText = new BitmapText(guiFont, false);
                hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
                hudText.setColor(ColorRGBA.Cyan);                             // font color
                hudText.setText(
                        // the text
                        "Position: " + cam.getLocation()
                        + "\nCamera direction: " + cam.getDirection()
                        + "\nDistance: " + cam.getLocation().length()
                        + "\nGravity acceleration: " + cam.getLocation().normalize().mult(-200f / cam.getLocation().lengthSquared()).length()
                        + "\nAmount of cannonballs created: " + quantity
                        + "\nCannon ball initial velocity: " + 5
                        + "\nCannon ball mass: " + 1
                        + "\nStandard gravitational parameter: " + 200
                );
                hudText.setLocalTranslation(300, hudText.getLineHeight() * 8, 0); // position
                guiNode.attachChild(hudText);
            }
        }

        t++;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
