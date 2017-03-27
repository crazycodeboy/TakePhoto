package darsh.multipleimageselect.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Darshan on 4/18/2015.
 */
public class Image implements Parcelable {
    public long id;
    public String name;
    public String path;
    public boolean isSelected;

    public Image(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
    }
}
