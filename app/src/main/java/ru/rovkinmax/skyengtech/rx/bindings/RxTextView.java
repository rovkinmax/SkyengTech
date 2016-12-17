package ru.rovkinmax.skyengtech.rx.bindings;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

public final class RxTextView {
    private RxTextView() {
    }

    @NonNull
    public static Observable<CharSequence> textChanges(@NonNull TextView textView) {
        return Observable.create(new TextViewTextOnSubscribe(textView));
    }


    private static class TextViewTextOnSubscribe implements ObservableOnSubscribe<CharSequence> {
        @NonNull
        private final TextView textView;

        private TextViewTextOnSubscribe(@NonNull TextView view) {
            textView = view;
        }

        @Override
        public void subscribe(ObservableEmitter<CharSequence> emitter) throws Exception {
            MainThreadDisposable.verifyMainThread();
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            textView.addTextChangedListener(textWatcher);
            emitter.setDisposable(mainThreadDisposable(() -> textView.removeTextChangedListener(textWatcher)));
            emitter.onNext(textView.getText());
        }

    }


    private static MainThreadDisposable mainThreadDisposable(@NonNull Runnable runnable) {
        return new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                runnable.run();
            }
        };
    }
}
