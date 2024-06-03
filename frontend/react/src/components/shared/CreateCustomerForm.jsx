import {Form, Formik, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Input, Select, Stack} from "@chakra-ui/react";
import {saveCustomer} from "../../services/client.js";
import {successNotification, errorNotification} from "../../services/notification.js";

const MyTextInput = ({label, ...props}) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

// And now we can use these
const CreateCustomerForm = ({ onSuccess }) => {
    return (
        <>
            <Formik
                initialValues={{
                    name: '',
                    email: '',
                    age: 0,
                    gender: '',
                    password: ''
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'En fazla 15 karakter girilebilir.')
                        .required('Zorunludur.'),
                    email: Yup.string()
                        .email('En fazla 20 karakter girilebilir.')
                        .required('Zorunludur.'),
                    age: Yup.number()
                        .min(16, 'En az 16 yaş girilebilir.')
                        .max(100, 'En fazla 100 yaş girilebilir.')
                        .required(),
                    password: Yup.string()
                        .min(4, 'En az 4 karakter girilebilir.')
                        .max(15, 'En fazla 15 veya daha az karakter girilebilir.')
                        .required('Zorunludur.'),
                    gender: Yup.string()
                        .oneOf(
                            ['MALE', 'FEMALE'],
                            'Geçersiz cinsiyet.'
                        )
                        .required('Required'),
                })}
                onSubmit={(customer, {setSubmitting}) => {
                    setSubmitting(true);
                    saveCustomer(customer)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Müşteri kaydedildi.",
                                `${customer.name} başarıyla kaydedildi.`
                            )
                            onSuccess(res.headers["authorization"]);
                        }).catch(err => {
                            console.log(err);
                            errorNotification(
                                err.code,
                                err.response.data.message
                            )
                    }).finally(() => {
                         setSubmitting(false);
                    })
                }}
            >
                {({isValid, isSubmitting}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Ad"
                                name="name"
                                type="text"
                                placeholder="Adınızı giriniz."
                            />

                            <MyTextInput
                                label="E-posta Adresi"
                                name="email"
                                type="email"
                                placeholder="E-posta adresinizi giriniz."
                            />

                            <MyTextInput
                                label="Yaş"
                                name="age"
                                type="number"
                                placeholder="Yaşınızı giriniz."
                            />

                            <MyTextInput
                                label="Şifre"
                                name="password"
                                type="password"
                                placeholder={"Şifrenizi giriniz. Şifreniz veritabanında hash'lenerek tutulacaktır."}
                            />

                            <MySelect label="Cinsiyet" name="gender">
                                <option value="">Cinsiyet Seç</option>
                                <option value="MALE">Erkek</option>
                                <option value="FEMALE">Kadın</option>
                            </MySelect>

                            <Button disabled={!isValid || isSubmitting} type="submit">Gönder</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default CreateCustomerForm;