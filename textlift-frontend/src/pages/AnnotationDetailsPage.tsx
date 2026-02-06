import { useParams } from "react-router-dom";
import AnnotationDetails from "../components/AnnotationDetails";
import bgPicture from "../assets/background_books.jpg";
export default function AnnotationDetailsPage() {
    const {documentId} = useParams<{documentId: string}>();
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
                    <section className="rounded-2xl border border-amber-950 bg-amber-950 bg-opacity-80 p-6 shadow-sm">
                        <AnnotationDetails documentId={documentId} />
                    </section>
                </main>
            </div>
        </div>
    );
}
