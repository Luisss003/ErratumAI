import { useState } from "react";
import FileUploader from "../components/FileUploader";
import { useNavigate } from "react-router-dom";
import bgPicture from "../assets/background_books.jpg";

type UploadResult =
  | { mode: "CACHE_HIT"; documentId: string }
  | { mode: "CACHE_HIT_WAIT" }
  | { mode: "NEW_UPLOAD"; uploadId: string };

export function UploadPage() {
  const navigate = useNavigate();
  const [message, setMessage] = useState<{ kind: "info" | "success"; text: string } | null>(null);

  return (
    <div
      className="relative min-h-screen text-slate-100"
      style={{
        backgroundImage: `url(${bgPicture})`,
        backgroundSize: "cover",
        backgroundPosition: "center",
      }}
    >
      <div className="pointer-events-none absolute inset-0 z-0 bg-black/60 backdrop-blur-sm" />

      <div className="relative z-10">
        <main className="mx-auto max-w-5xl px-4 py-10">
          <header className="mb-6">
            <h2 className="mt-1 text-center text-xl text-white">
              Upload a PDF to generate annotations and corrections.
            </h2>
          </header>

          {message && (
            <div
              className={
                "mb-4 rounded-2xl border p-4 text-sm " +
                (message.kind === "success"
                  ? "border-emerald-900/60 bg-emerald-950/30 text-emerald-200"
                  : "border-slate-800 bg-slate-900 text-slate-200")
              }
            >
              {message.text}
            </div>
          )}

          <section className="rounded-2xl border border-amber-950 bg-amber-950 bg-opacity-80 p-6 shadow-sm">
            <FileUploader
              onResult={(result: UploadResult) => {
                if (result.mode === "CACHE_HIT") {
                  setMessage({
                    kind: "success",
                    text: "Annotations already exist for this file. Redirecting…",
                  });
                  navigate(`/annotations/document/${result.documentId}`);
                  return;
                }

                if (result.mode === "CACHE_HIT_WAIT") {
                  setMessage({
                    kind: "info",
                    text: "This file is currently being processed. Check back soon.",
                  });
                  navigate("/");
                  return;
                }
              }}
            />
          </section>

          <div className="mt-6">
            <button
              type="button"
              onClick={() => navigate("/")}
              className="rounded-xl px-4 py-2 text-sm font-semibold text-slate-950 bg-red-500 hover:bg-red-400 active:bg-red-600"
            >
              Back to home
            </button>
          </div>
        </main>
      </div>
    </div>
  );
}
