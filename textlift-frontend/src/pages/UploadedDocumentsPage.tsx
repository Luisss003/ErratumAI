
import DocumentPreview from "../components/DocumentPreview";

export default function UploadedDocumentsPage() {
  return (
    <div className="min-h-screen bg-black text-slate-100">
      <main className="mx-auto max-w-5xl px-4 py-10">
        <header className="mb-6">
          <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
            Uploaded Documents
          </h1>
          <p className="mt-1 text-sm text-slate-400">
            Click a document to open its annotations.
          </p>
        </header>

        <section className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
          <DocumentPreview />
        </section>
      </main>
    </div>
  );
}
