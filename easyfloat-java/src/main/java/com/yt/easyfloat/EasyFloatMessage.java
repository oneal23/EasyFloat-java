package com.yt.easyfloat;

public interface EasyFloatMessage {

    String WARN_PERMISSION = "No permission exception. You need to turn on overlay permissions.";

    String WARN_NO_LAYOUT = "No layout exception. You need to set up the layout file.";

    String WARN_UNINITIALIZED = "Uninitialized exception. You need to initialize in the application.";

    String WARN_REPEATED_TAG = "Tag exception. You need to set different EasyFloat tag.";

    String WARN_CONTEXT_ACTIVITY = "Context exception. Activity float need to pass in a activity context.";

    String WARN_CONTEXT_REQUEST = "Context exception. Request Permission need to pass in a activity context.";

}
