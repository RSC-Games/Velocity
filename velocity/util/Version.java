package velocity.util;

public class Version {
    static final Version invalid = new Version(0, 0, 0, 0);
    int v;
    int major;
    int minor;
    int patch;

    public Version(int major, int minor, int patch) {
        this.v = 1;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(int v, int major, int minor, int patch) {
        this.v = v;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String all) {
        if (all.length() != 7) {
            System.out.println("[util.Version]: Invalid version code (length != 7)!");
            return;
        }
        this.v = Integer.parseInt(all.substring(0, 1));
        this.major = Integer.parseInt(all.substring(1, 3));
        this.minor = Integer.parseInt(all.substring(3, 5));
        this.patch = Integer.parseInt(all.substring(5, 7));
    }

    public boolean isNewer(Version other) {
        if (this.v > other.v) { return true; }
        else if (this.major > other.major) { return true; }
        else if (this.minor > other.minor) { return true; }
        else if (this.patch > other.patch) { return true; }
        return false;
    }

    public boolean isOlder(Version other) {
        if (this.v >= other.v) { return false; }
        else if (this.major >= other.major) { return false; }
        else if (this.minor >= other.minor) { return false; }
        else if (this.patch >= other.patch) { return false; }
        return true;
    }

    public boolean equals(Version other) {
        return this.v == other.v && this.major == other.major && this.minor == other.minor
            && this.patch == other.patch;
    }

    public String toIntString() {
        String out = "" + this.v;
        out += ("" + (this.major + 100)).substring(1, 3);
        out += ("" + (this.minor + 100)).substring(1, 3);
        out += ("" + (this.patch + 100)).substring(1, 3);
        return out;
    }

    public String toString() {
        return v + "." + major + "." + minor + "." + patch;
    }
}
