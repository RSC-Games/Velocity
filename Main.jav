// Application main code. Must call into VelocityMain.
// NOT PART OF VELOCITY!
// As such, it has been excluded from the build by renaming it to
// Main.jav instead of Main.java

//import appcode.AppConfig;
//import appcode.SceneDefs;
import velocity.GlobalAppConfig;
import velocity.VelocityMain;

public class Main {
    public static void main(String[] args) {
        VelocityMain.app_main(new GlobalAppConfig(), new TestSceneDefs());
    }
}