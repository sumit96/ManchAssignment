package practice.mvp.com.manchassignment.types;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 9/17/18.
 */

public class PostItem  implements Parcelable {

    private String title;
    private String description;
    private String imagePath;



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imagePath);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public PostItem createFromParcel(Parcel in) {
            return new PostItem(in);
        }

        public PostItem[] newArray(int size) {
            return new PostItem[size];
        }
    };

    public PostItem(Parcel in) {
        title = in.readString();
        description = in.readString();
        imagePath = in.readString();
    }
    public PostItem(String title, String description, String imagePath)
    {
        this.title=title;
        this.description=description;
        this.imagePath=imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }
}
