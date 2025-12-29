import { getAnnotationByDocumentId } from "../api/apiRequests";
import { useEffect, useState } from "react";

export default function AnnotationDetailsPage({
  documentId,
}: {
  documentId?: string;
}) {
  const [annotation, setAnnotation] = useState<unknown>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!documentId) return;

    let cancelled = false;

    (async () => {
      try {
        setLoading(true);
        setError(null);

        const data = await getAnnotationByDocumentId(documentId);
        if (!cancelled) setAnnotation(data);
      } catch (e) {
        if (!cancelled) setError("Failed to load annotation.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [documentId]);

  if (!documentId) return <div>No Document ID provided</div>;
  if (loading) return <div>Loading…</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h1>Annotation Details for Document ID: {documentId}</h1>

      <pre
        style={{
          marginTop: 12,
          padding: 16,
          borderRadius: 12,
          border: "1px solid #e5e7eb",
          background: "#0b1020",
          color: "#e5e7eb",
          overflowX: "auto",
          fontSize: 13,
          lineHeight: 1.5,
        }}
      >
        {JSON.stringify(annotation, null, 2)}
      </pre>
    </div>
  );
}
