import { Link } from "react-router-dom";
import { Card } from "../components/ui/Card";
import { Navbar } from "../layout/Navbar";
import videoBg from "../assets/background.mp4";

export function HomePage() {
  return (
    <div className="min-h-screen text-slate-100 font-mono">
      
      <div className="fixed inset-0 -z-10">
        <video src={videoBg} autoPlay loop muted className="fixed inset-0 object-cover w-full h-full z-[-1] opacity-25"></video>
        <div className="absolute inset-0 bg-amber-950 bg-opacity-35" />
      </div>
      
      <Navbar />
      <main className="mx-auto max-w-6xl px-4 py-10">
        
        <section className="rounded-2xl bg-slate-500 bg-opacity-20 p-6 shadow-sm">
          {/* Hero section with app description */}
          <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
            <div className="space-y-2">
              <h1 className="text-2xl font-bold tracking-tight md:text-3xl text-slate-900">
                Revive the usability of old textbooks and papers.
              </h1>
              <p className="max-w-xl text-sm font-bold leading-relaxed text-slate-900">
                Upload your old textbooks and research papers as PDFs, and let ErratumAI
                find and correct outdated, incorrect, or biased information within them. 
              </p>
            </div>


            {/* Call to action buttons */}
            <div className="flex gap-2">
              <Link
                to="/upload"
                className="text-center bg-slate-100 border border-slate-100 rounded-md px-4 py-2 text-sm font-semibold text-slate-800 hover:bg-opacity-55 hover:border-slate-300"
              >
                Start an upload
              </Link>
              <Link
                to="/documents"
                className="text-center rounded-md border border-slate-800 bg-slate-950 px-4 py-2 text-sm font-semibold text-slate-100 hover:bg-opacity-55 hover:border-slate-300"
              >
                View documents
              </Link>
            </div>
          </div>
        </section>


        {/* Feature cards */}
        <section className="mt-6 grid gap-4 md:grid-cols-2">
          <Card
            title="Upload PDF"
            desc="Create a new upload session and send a file."
            to="/upload"
            cta="Upload"
          />
          <Card
            title="Browse documents"
            desc="See processed PDFs and their current status."
            to="/documents"
            cta="Open list"
          />
        </section>

        {/* moneygrab :) */}
            <section className="mt-6 text-center rounded-2xl bg-slate-500 bg-opacity-20 p-6">
            <div className="space-y-1">
                <h1 className="text-xl font-semibold text-slate-900">Account Tiers</h1>
                <p className="text leading-relaxed text-slate-900">
                There isn't a difference between account tiers! You can choose to upgrade
                your TextLift account simply if you want to help with the solo development
                and improvement of TextLift.
                </p>
            </div>

            <div className="mt-4">
            <button
                type="button"
                onClick={() => alert("Coming soon :)")}
                className="inline-flex items-center justify-center rounded-xl bg-red-900 bg-opacity-40 px-4 py-2 text-sm font-semibold text-slate-950 hover:bg-opacity-60"
            >
                Pro Tier — $1/month
            </button>
            </div>
        </section>
      </main>
    </div>
  );
}


