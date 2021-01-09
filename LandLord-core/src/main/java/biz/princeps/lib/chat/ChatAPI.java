package biz.princeps.lib.chat;

/**
 * Project: PrincepsLib
 * Created by Alex D. (SpatiumPrinceps)
 * Date: 07/18/17
 * <p>
 * This class can be used to generate new messages. Just wraps around existing classes
 */
public class ChatAPI {

    /**
     * Returns a new instance of a multipagedmessage builder.
     * The builder needs to be supplied by various parameters @see#MultiPagedMessage.Builder
     *
     * @return a new instance
     */
    public static MultiPagedMessage.Builder createMultiPagedMessge() {
        return new MultiPagedMessage.Builder();
    }

    /**
     * Creates a new MultiPagedComponentMessage Builder. This multipagedmessage does not use strings, but componentmessages, so allows nice stuff
     *
     * @return a new instance
     */
    public static MultiPagedComponentMessage.Builder createMultiPagedComponnentMessage() {
        return new MultiPagedComponentMessage.Builder();
    }
}
