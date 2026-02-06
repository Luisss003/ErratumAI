import { Link } from "react-router-dom";

export function Card({title, desc, to, cta, subtle}: {
  title: string;
  desc: string;
  to: string;
  cta: string;
  subtle?: boolean;
}) {
  return (
    <div
      className={
        "text-center rounded-2xl border p-5 shadow-sm " +
        (subtle
          ? "border-slate-300 bg-slate-500 bg-opacity-20 border-opacity-30"
          : "border-slate-300 bg-slate-500 bg-opacity-20 border-opacity-50")
      }
    >
      <div className="text-xl text-slate-900 font-semibold">{title}</div>
      <p className="mt-1 text-lg leading-relaxed text-slate-700">{desc}</p>

      <Link
        to={to}
        className="mt-2 inline-flex items-center justify-center rounded-md bg-slate-500 bg-opacity-20 px-3 py-2 text-md font-semibold text-slate-900 hover:bg-opacity-30"
      >
        {cta}
      </Link>
    </div>
  );
}