
import DocumentPreview from "../components/DocumentPreview";
import bgPicture from "../assets/background_books.jpg";

export default function UploadedDocumentsPage() {
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
            <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
              Uploaded Documents
            </h1>
            <p className="mt-1 text-sm text-slate-400">
              Click a document to open its annotations.
            </p>
          </header>

          <section className="rounded-2xl border border-amber-950 bg-amber-950 bg-opacity-80 p-6 shadow-sm">
            <DocumentPreview />
          </section>
        </main>
      </div>
    </div>
  );
}
