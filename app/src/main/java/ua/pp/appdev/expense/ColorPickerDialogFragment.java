package ua.pp.appdev.expense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ua.pp.appdev.expense.ColorPickerDialogFragment.OnColorSelectedListener} interface
 * to handle interaction events.
 * Use the {@link ColorPickerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ColorPickerDialogFragment extends DialogFragment {
    private static final String COLOR = "color";

    // Исходный цвет
    private int color;

    // Флаг того, что исходный цвет был передан
    private boolean colorPresent;

    // Слущатель события выбора цвета
    private OnColorSelectedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param color Начальный цвет
     * @return A new instance of fragment ColorPickerDialogFragment.
     */
    public static ColorPickerDialogFragment newInstance(int color) {
        ColorPickerDialogFragment fragment = new ColorPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    public ColorPickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_color_picker_dialog, null);
        final ColorPicker picker = (ColorPicker) dialogView.findViewById(R.id.picker);

        // Если исходный цвет представлен - установим его
        if (colorPresent) {
            picker.setColor(this.color);
        }

        // Создадим диалог
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setView(dialogView)
                .setTitle(getString(R.string.choose_color))
                .setPositiveButton(R.string.done,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.onColorSelected(picker.getColor());
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                )
                .create();

        // Отключаем отображение изначального цвета
        picker.setShowOldCenterColor(false);
        return dialog;
    }
    


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.color = getArguments().getInt(COLOR);
        } else {
            this.colorPresent = false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnColorSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnColorSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Интерфейс, с помощью которого передается выбранный цвет
     */
    public interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }

}
