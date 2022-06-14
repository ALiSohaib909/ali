package com.pins.infinity.modules

import com.pins.infinity.viewModels.*
import com.pins.infinity.viewModels.PaymentPlanViewModel.ChosenOption
import com.pins.infinity.viewModels.antitheft.*
import com.pins.infinity.viewModels.applock.*
import com.pins.infinity.viewModels.authorisation.LoginRegisterViewModel
import com.pins.infinity.viewModels.base.BaseMainViewModel
import com.pins.infinity.viewModels.emailVerification.EmailVerifyConfirmViewModel
import com.pins.infinity.viewModels.emailVerification.EmailVerifyViewModel
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */

val viewModelModule = module {
    viewModel<PaymentViewModel>()
    viewModel { (paymentOption: String) -> PaymentPlanViewModel(get(), get(), get(), get(), paymentOption) }
    viewModel { (chosenOption: ChosenOption) -> PaymentSummaryViewModel(get(), get(), get(), chosenOption) }
    viewModel { (chosenOption: ChosenOption) -> PaymentCodeViewModel(get(), get(), chosenOption) }
    viewModel { (isSuccessParameter: Boolean) -> PaymentResultViewModel(get(), isSuccessParameter) }
    viewModel<PaymentUssdViewModel>()
    viewModel { (isDial: Boolean) -> PaymentUssdProgressViewModel(get(), get(), isDial) }
    viewModel { (chosenOption: ChosenOption, sku: String) -> PaymentGoogleViewModel(get(), get(), chosenOption, sku) }
    viewModel { (lockAction: LockAction) -> SetupPinViewModel(get(), get(), get(), lockAction, get()) }
    viewModel<BaseMainViewModel>()
    viewModel<EmailVerifyViewModel>()
    viewModel { (isSuccess: Boolean) -> EmailVerifyConfirmViewModel(get(), isSuccess) }
    viewModel<LoginRegisterViewModel>()
    viewModel<AntiTheftViewModel>()
    viewModel { (option: AntiTheftCommand) -> AntiTheftOptionViewModel(get(), get(), option) }
    viewModel { (lockPackage: String) -> AppLockPinViewModel(get(), get(), lockPackage, get(), get(), get()) }
    viewModel<AppLockViewModel>()
    viewModel<AppLockForgotPinViewModel>()
    viewModel { (initRecoveryToken: String) -> ForgotPinCheckCodeViewModel(get(), get(), initRecoveryToken) }
    viewModel { (checkRecoveryToken: String) -> ForgotPinNewPinViewModel(get(), get(), checkRecoveryToken) }
    viewModel<ForgotPinSumUpViewModel>()
}