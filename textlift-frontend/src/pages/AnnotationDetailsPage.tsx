import { useParams } from "react-router-dom";
import AnnotationDetails from "../components/AnnotationDetails";
export default function AnnotationDetailsPage() {
    const {documentId} = useParams<{documentId: string}>();
    return (
        <div>
            <AnnotationDetails documentId={documentId} />
        </div>
    )
}