import { getErrorMsg } from "../api/http";
import { useState } from "react";
import { useForm, type SubmitHandler } from "react-hook-form";
import { useNavigate, NavLink } from "react-router-dom";
import type { SignupRequest } from "../api/apiRequests";
import { signup } from "../api/apiRequests";
import { Navbar } from "../layout/Navbar";
import bgPicture from "../assets/background_books.jpg"

type SignupForm = SignupRequest & { confirmPassword: string };


export function RegisterPage() {
  
  const navigate = useNavigate();
  const [serverError, setServerError] = useState<string | null>(null);

  //Set up for using form hook
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<SignupForm>({
    defaultValues: { fullName: "", email: "", password: "", confirmPassword: "" },
    mode: "onTouched",
  });

  const password = watch("password");

  const onSubmit: SubmitHandler<SignupForm> = async (data) => {
    setServerError(null);
    try {
      //Confirm password is not sent with the request, its only for client side validation
      const { confirmPassword, ...payload } = data;
      await signup(payload);

      //Redirect user to their documents page after successful registration
      navigate("/documents", { replace: true });
    } catch (err) {
      setServerError(getErrorMsg(err));
    }
  };

  return (
    <div className="min-h-screen text-slate-100"
      style={{ backgroundImage: `url(${bgPicture})`, backgroundSize: "cover", backgroundPosition: "center" }}>
      <div className="pointer-events-none absolute inset-0 z-0 bg-black/60 backdrop-blur-sm" />
      
      <div className="relative z-10">
        <Navbar />

        <main className="mx-auto max-w-5xl px-4 py-10">
          <div className="mx-auto w-full max-w-md rounded-2xl border border-amber-950 bg-amber-950 bg-opacity-80 p-6 shadow-sm">
            <div className="space-y-1">
              <h1 className="text-xl font-bold tracking-tight">Create your account</h1>
            </div>

            { /* Display server error if registration fails */}
            {serverError && (
              <div className="mt-4 rounded-xl border border-red-900/50 bg-slate-950 p-3 text-sm text-red-300">
                {serverError}
              </div>
            )}

            {/* Registration form */}
            <form className="mt-5 space-y-4" onSubmit={handleSubmit(onSubmit)}>
              {/* Full name field */}
              <div>
                <label className="mb-1 block text-xs font-semibold text-slate-300">
                  Full name
                </label>
                <input
                  {...register("fullName", { required: "Full name is required" })}
                  disabled={isSubmitting}
                  type="text"
                  autoComplete="name"
                  placeholder="John Smith"
                  className={[
                    "w-full rounded-xl border bg-slate-950 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600",
                    "focus:outline-none focus:ring-2 focus:ring-cyan-500/40",
                    errors.fullName ? "border-red-600" : "border-slate-800",
                    isSubmitting ? "opacity-70" : "",
                  ].join(" ")}
                />
                {errors.fullName && (
                  <div className="mt-1 text-xs text-red-400">{errors.fullName.message}</div>
                )}
              </div>

              {/* Email field */}
              <div>
                <label className="mb-1 block text-xs font-semibold text-slate-300">Email</label>
                <input
                  {...register("email", {
                    required: "Email is required",
                    pattern: {
                      value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                      message: "Invalid email address",
                    },
                  })}
                  disabled={isSubmitting}
                  type="email"
                  inputMode="email"
                  autoComplete="email"
                  placeholder="you@example.com"
                  className={[
                    "w-full rounded-xl border bg-slate-950 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600",
                    "focus:outline-none focus:ring-2 focus:ring-cyan-500/40",
                    errors.email ? "border-red-600" : "border-slate-800",
                    isSubmitting ? "opacity-70" : "",
                  ].join(" ")}
                />
                {errors.email && (
                  <div className="mt-1 text-xs text-red-400">{errors.email.message}</div>
                )}
              </div>

              {/* Password field */}
              <div>
                <label className="mb-1 block text-xs font-semibold text-slate-300">
                  Password
                </label>
                <input
                  {...register("password", {
                    required: "Password is required",
                    minLength: { value: 6, message: "Password must be at least 6 characters long" },
                  })}
                  disabled={isSubmitting}
                  type="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  className={[
                    "w-full rounded-xl border bg-slate-950 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600",
                    "focus:outline-none focus:ring-2 focus:ring-cyan-500/40",
                    errors.password ? "border-red-600" : "border-slate-800",
                    isSubmitting ? "opacity-70" : "",
                  ].join(" ")}
                />
                {errors.password && (
                  <div className="mt-1 text-xs text-red-400">{errors.password.message}</div>
                )}
              </div>

              {/* Confirm password field (only used for client side verification)*/}
              <div>
                <label className="mb-1 block text-xs font-semibold text-slate-300">
                  Confirm password
                </label>
                <input
                  {...register("confirmPassword", {
                    required: "Please confirm your password",
                    validate: (v) => v === password || "Passwords do not match",
                  })}
                  disabled={isSubmitting}
                  type="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  className={[
                    "w-full rounded-xl border bg-slate-950 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600",
                    "focus:outline-none focus:ring-2 focus:ring-cyan-500/40",
                    errors.confirmPassword ? "border-red-600" : "border-slate-800",
                    isSubmitting ? "opacity-70" : "",
                  ].join(" ")}
                />
                {errors.confirmPassword && (
                  <div className="mt-1 text-xs text-red-400">
                    {errors.confirmPassword.message}
                  </div>
                )}
              </div>

              {/* Submit button */}
              <button
                disabled={isSubmitting}
                type="submit"
                className={[
                  "w-full rounded-xl px-4 py-2 text-md font-semibold",
                  "bg-red-500 text-slate-950 hover:bg-red-400 active:bg-red-600",
                  "disabled:cursor-not-allowed disabled:opacity-70",
                ].join(" ")}
              >
                {isSubmitting ? "Registering..." : "Create account"}
              </button>

              {/* Link to login page for users who already have an account */}
              <div className="flex items-center justify-between text-base text-slate-400">
                <span>Already have an account?</span>
                <NavLink to="/login" className="font-semibold text-cyan-300 hover:text-cyan-200">
                  Log in
                </NavLink>
              </div>
            </form>
          </div>
        </main>
      </div>
      </div>
  );
}
