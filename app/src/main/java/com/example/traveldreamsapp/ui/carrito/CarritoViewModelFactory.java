package com.example.traveldreamsapp.ui.carrito;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.traveldreamsapp.repository.CarritoRepository;
import androidx.annotation.NonNull;


public class CarritoViewModelFactory implements ViewModelProvider.Factory {
    private final CarritoRepository repository;

    public CarritoViewModelFactory(CarritoRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CarritoViewModel.class)) {
            return (T) new CarritoViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}