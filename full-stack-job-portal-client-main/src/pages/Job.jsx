import React, { useState } from "react";
import styled from "styled-components";
import { useParams } from "react-router-dom";

import { useQuery } from "@tanstack/react-query";
import { getSingleHandler, buildApiUrl } from "../utils/FetchHandlers";
import LoadingComTwo from "../components/shared/LoadingComTwo";

import advancedFormat from "dayjs/plugin/advancedFormat";
import dayjs from "dayjs";
dayjs.extend(advancedFormat);

import { MdAccessTime } from "react-icons/md";
import Navbar from "../components/shared/Navbar";
import { useUserContext } from "../context/UserContext";
import Swal from "sweetalert2";
import axios from "axios";

// import advancedFormat from "dayjs/plugin/advancedFormat";
// import dayjs from "dayjs";
dayjs.extend(advancedFormat);

const Job = () => {
    const { id } = useParams();
    const { user } = useUserContext();
    const {
        isLoading,
        isError,
        data: job,
        error,
    } = useQuery({
        queryKey: ["job"],
        queryFn: () =>
            getSingleHandler(
                buildApiUrl(`/api/v1/jobs/${id}`)
            ),
    });

    const date = dayjs(job?.jobDeadline).format("MMM Do, YYYY");

    const handleApply = async () => {
        if (!user) {
            Swal.fire({
                icon: "warning",
                title: "Please Login",
                text: "You need to login to apply for a job",
            });
            return;
        }

        let currentDate = new Date();
        let dateStr = currentDate.toISOString().slice(0, 10);

        // Show file input dialog
        const { value: file } = await Swal.fire({
            title: "Apply for Job",
            html: `
                <div style="text-align: left;">
                    <p style="margin-bottom: 10px;">Upload your resume (Optional - PDF or DOC)</p>
                    <input type="file" id="resume-input" accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document" style="width: 100%;"/>
                </div>
            `,
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Apply",
            cancelButtonText: "Cancel",
            didOpen: (modal) => {
                document.getElementById("resume-input").focus();
            },
            preConfirm: () => {
                const fileInput = document.getElementById("resume-input");
                const selectedFile = fileInput.files[0];
                
                if (selectedFile) {
                    const allowedTypes = ["application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"];
                    if (!allowedTypes.includes(selectedFile.type)) {
                        Swal.showValidationMessage("Only PDF and DOC files are allowed");
                        return false;
                    }
                }
                return selectedFile || null;
            },
        });

        if (file === undefined) {
            return; // User cancelled
        }

        try {
            const formData = new FormData();
            formData.append("applicantId", user?._id);
            formData.append("recruiterId", job?.createdBy);
            formData.append("jobId", id);
            formData.append("status", "pending");
            formData.append("dateOfApplication", dateStr);
            
            if (file) {
                formData.append("resume", file);
            }

            const response = await axios.post(
                buildApiUrl("/api/v1/application/apply"),
                formData,
                {
                    withCredentials: true,
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            Swal.fire({
                icon: "success",
                title: "Hurray...",
                text: response?.data?.message,
            });
        } catch (error) {
            console.log(error);
            if (error?.response?.data?.error) {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: error?.response?.data?.error[0].msg,
                });
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: error?.response?.data || error.message,
                });
            }
        }
    };

    if (isLoading) {
        return <LoadingComTwo />;
    }
    if (isError) {
        return <h2 className="">{error?.message}</h2>;
    }
    // if (job) {
    //     console.log(job.result);
    // }
    return (
        <>
            <Navbar />
            <Wrapper>
                <div className="top-row">
                    <h2 className="title">
                        <span className="capitalize ">job title: </span>
                        {job?.position}
                    </h2>
                    <h4 className="company">
                        <span className="fancy">posted by: </span>
                        {job?.company}
                    </h4>
                    <h4 className="post-date">
                        <MdAccessTime className="text-lg mr-1" />
                        {dayjs(job?.result?.createdAt).format("MMM Do, YYYY")}
                    </h4>
                </div>
                <div className="middle-row">
                    <div className="description">
                        <h3 className="sec-title">description</h3>
                        <p className="">{job?.jobDescription}</p>
                    </div>
                    <h4 className="deadline">
                        Deadline: <span className="">{date}</span>
                    </h4>
                    <h4 className="vacancy">
                        Job Vacancy: <span className="">{job?.jobVacancy}</span>
                    </h4>
                    <div className="requirement">
                        <h3 className="sec-title">Requirements</h3>
                        <ul>
                            {job?.jobSkills?.map((skill) => (
                                <li key={skill}>{skill}</li>
                            ))}
                        </ul>
                    </div>
                    <div className="facility">
                        <h3 className="sec-title">Facilities</h3>
                        <ul>
                            {job?.jobFacilities?.map((facility) => (
                                <li key={facility}>{facility}</li>
                            ))}
                        </ul>
                    </div>
                    <h4 className="salary">
                        Salary: <span className="">{job?.jobSalary} TK</span>
                    </h4>
                    <div className="apply">
                        <h3 className="sec-title">To apply</h3>
                        {user?.role === "user" ? (
                            <button onClick={handleApply} className="apply-btn">
                                Apply Now
                            </button>
                        ) : (
                            <>
                                <p className="intro">send your cv/resume</p>
                                <p className="info">Email: {job?.jobContact}</p>
                            </>
                        )}
                    </div>
                </div>
            </Wrapper>
        </>
    );
};

const Wrapper = styled.section`
    padding: 2rem 0;
    max-width: 1000px;
    margin: 0 auto;
    margin-bottom: calc(20px + 1vw);
    width: 100%;

    .top-row {
        margin-bottom: calc(30px + 1vw);
    }
    .top-row .title {
        font-size: calc(14px + 1vw);
        text-align: center;
    }
    .top-row .company {
        font-size: calc(11px + 0.35vw);
        text-align: center;
        text-transform: capitalize;
        font-weight: 600;
        margin-top: 4px;
        opacity: 0.75;
    }
    .top-row .post-date {
        font-size: 11px;
        font-weight: 600;
        text-transform: capitalize;
        text-align: center;
        opacity: 0.75;
        margin-top: 8px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .middle-row .description h3 {
        font-size: calc(14px + 0.15vw);
        font-weight: 600;
        text-transform: capitalize;
        opacity: 0.8;
        text-decoration: underline;
    }
    .middle-row .description p {
        margin-top: 6px;
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
        text-align: justify;
        line-height: 23px;
    }
    .middle-row .deadline {
        font-size: calc(13px + 0.1vw);
        font-weight: 600;
        opacity: 0.8;
        margin-top: calc(10px + 0.3vw);
    }
    .middle-row .vacancy {
        font-size: calc(13px + 0.1vw);
        font-weight: 600;
        opacity: 0.8;
        margin-top: 4px;
        margin-bottom: calc(10px + 0.3vw);
    }
    .middle-row .requirement {
        margin-bottom: calc(10px + 0.3vw);
    }
    .middle-row .requirement .sec-title {
        font-size: calc(14px + 0.15vw);
        font-weight: 600;
        text-transform: capitalize;
        opacity: 0.8;
        text-decoration: underline;
    }
    .middle-row .requirement p {
        margin-top: 6px;
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
        text-align: justify;
        line-height: 23px;
    }
    .middle-row .requirement ul {
        margin-top: 6px;
        list-style: circle;
        margin-left: calc(30px + 0.5vw);
    }
    .middle-row .requirement ul li {
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
        text-transform: capitalize;
        padding: 2px 0;
    }

    .middle-row .facility .sec-title {
        font-size: calc(14px + 0.15vw);
        font-weight: 600;
        text-transform: capitalize;
        opacity: 0.8;
        text-decoration: underline;
    }
    .middle-row .facility {
        margin-bottom: calc(10px + 0.3vw);
    }
    .middle-row .facility p {
        margin-top: 6px;
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
        text-align: justify;
        line-height: 23px;
    }
    .middle-row .facility ul {
        margin-top: 6px;
        list-style: circle;
        margin-left: calc(30px + 0.5vw);
    }
    .middle-row .facility ul li {
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
        text-transform: capitalize;
        padding: 2px 0;
    }
    .middle-row .salary {
        font-size: calc(14px + 0.1vw);
        font-weight: 600;
        opacity: 0.85;
        margin-bottom: calc(10px + 0.3vw);
    }
    .middle-row .apply h3 {
        font-size: calc(14px + 0.15vw);
        font-weight: 600;
        text-transform: capitalize;
        opacity: 0.8;
        text-decoration: underline;
    }
    .middle-row .apply p {
        margin-top: 6px;
        font-size: calc(12px + 0.15vw);
        font-weight: 400;
        opacity: 0.95;
    }
    .middle-row .apply p.intro {
        text-transform: capitalize;
    }
    .middle-row .apply p.info {
        font-weight: 600;
        opacity: 0.8;
    }
    .middle-row .apply .apply-btn {
        margin-top: 12px;
        padding: 8px 16px;
        background-color: #3b82f6;
        color: white;
        border: none;
        border-radius: 4px;
        font-size: calc(12px + 0.15vw);
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        text-transform: capitalize;

        &:hover {
            background-color: #2563eb;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }

        &:active {
            transform: scale(0.98);
        }
    }
`;

export default Job;
