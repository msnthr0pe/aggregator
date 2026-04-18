# Стили проекта

## Общее
* Внешние отступы элементов не вносить в style, лучше прописывать внутри layout

## Компоненты проекта

### MaterialToolbar

#### Параметры
* android:id - id для кнопки назад
* layout_width (обязательный параметр)
* layout_height (обязательный параметр) - установлен дефолтный размер в теме, но прописывать обязательно (toolbar_height)
* menu - если нужны боковые кнопки, то подставить нужный тип меню (toolbar_filter, toolbar_share_like)
* title - название
* navigationIcon - иконка "Назад" 

#### Пример
```
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/btn_back"
        android:layout_width="match_parent"
        android:layout_height="@dimen/toolbar_height"
        app:menu="@menu/toolbar_share_like"
        app:title="Какое-то название"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:navigationIcon="@drawable/arrow_left" />
```

### Button

## Общее
* Нужно использовать com.google.android.material.button.MaterialButton
* Размеры кнопки задавать внутри layout
* Использовать стили ButtonPrimary, ButtonTextError, ButtonFind

#### Пример
```
    <com.google.android.material.button.MaterialButton
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:text="Какое-то название"
        style="@style/ButtonFind" />
```
