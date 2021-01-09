package biz.princeps.landlord.api.tuple;

public final class Pair<T,V> {
    private final T left;
    private final V right;

    private Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <T,V> Pair<T,V> of(T left, V right){
        return new Pair<>(left,right);
    }

    public T getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }
}
