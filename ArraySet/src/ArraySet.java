import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {
    private List<E> set;
    private Comparator<? super E> comparator;


    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Collection<? extends E> c) {
        this(c,null);

    }

    private ArraySet(ArraySet<E> c, int fromIndex, int toIndex) {
        this.set = c.set.subList(fromIndex, toIndex);
        this.comparator = c.comparator;
    }

    public ArraySet(Collection<? extends E> c, Comparator<? super  E> comparator) {
        set = new ArrayList<>();
        this.comparator = comparator;
        ArrayList<E> copyList = new ArrayList<>(c);
        copyList.sort(this.comparator);
        if (!copyList.isEmpty()) {
            set.add(copyList.get(0));
        }
        for (int i = 0; i < copyList.size() - 1; ++i) {
            if (this.comparator == null) {
                if (!copyList.get(i).equals(copyList.get(i+1))) {
                    set.add(copyList.get(i + 1));
                }
            } else {
                if (this.comparator.compare(copyList.get(i), copyList.get(i + 1)) != 0) {
                    set.add(copyList.get(i + 1));
                }
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size() && set.get(currentIndex) != null;
            }

            @Override
            public E next() {
                return set.get(currentIndex++);
            }
        };
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean contains(Object o) {
        return  Collections.binarySearch(set, (E) o, comparator) >= 0;
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    private int getIndex(E element) {
        int index = Collections.binarySearch(set, element, comparator);
        return index < 0 ? Math.abs(index) - 1 : index;
    }

    private SortedSet<E> subSetHelper (E fromElement, E toElement) {
        if (fromElement == null) {
            int index = getIndex(toElement);
            return new ArraySet<>(this, 0, index);
        } if (toElement == null) {
            int index = getIndex(fromElement);
            return new ArraySet<>(this, index, size());
        } else {
            int fromIndex = getIndex(fromElement);
            int toIndex = getIndex(toElement);
            return new ArraySet<>(this, fromIndex, toIndex);
        }
    }


    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (fromElement == null || toElement == null) {
            throw new NullPointerException("Null value of the arguments is prohibited");
        }
        return subSetHelper(fromElement, toElement);
    }


    @Override
    public SortedSet<E> headSet(E toElement) {
        if (toElement == null && !contains(null)) {
            throw new NullPointerException("Null value of the argument is prohibited");
        }
        return subSetHelper(null, toElement);

    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        if (fromElement == null && !contains(null)) {
            throw new NullPointerException("Null value of the argument is prohibited");
        }
        return subSetHelper(fromElement, null);
    }

    @Override
    public E first() {
        if (!isEmpty()) {
            return set.get(0);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E last() {
        if (!isEmpty()) {
            return set.get(size() - 1);
        } else {
            throw new NoSuchElementException();
        }
    }
}
