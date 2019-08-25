package com.example.renderer.view.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoSelectionModel<T> extends MultipleSelectionModel<T> {

    public static <T> NoSelectionModel<T> getInstance() {
        return new NoSelectionModel<>();
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return FXCollections.emptyObservableList();
    }

    @Override
    public void selectIndices(int i, int... ints) {}

    @Override
    public void selectAll() {}

    @Override
    public void clearAndSelect(int i) {}

    @Override
    public void select(int i) {}

    @Override
    public void select(T t) {}

    @Override
    public void clearSelection(int i) {}

    @Override
    public void clearSelection() {}

    @Override
    public boolean isSelected(int i) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void selectPrevious() {}

    @Override
    public void selectNext() {}

    @Override
    public void selectFirst() {}

    @Override
    public void selectLast() {}
}
