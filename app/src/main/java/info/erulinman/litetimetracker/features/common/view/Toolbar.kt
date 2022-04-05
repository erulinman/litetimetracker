package info.erulinman.litetimetracker.features.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import info.erulinman.litetimetracker.databinding.LayoutToolbarBinding

class Toolbar(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val binding = LayoutToolbarBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setTitle(text: CharSequence) {
        binding.tvTitle.text = text
    }

    fun setTitle(@StringRes resId: Int) = binding.tvTitle.setText(resId)

    fun setActionIcon(@DrawableRes drawableId: Int) =
        binding.btnAction.setImageResource(drawableId)

    fun setOnActionClickListener(action: OnClickListener) =
        binding.btnAction.setOnClickListener(action)

    fun setActionVisibility(isVisible: Boolean) {
        binding.btnAction.isVisible = isVisible
    }
}