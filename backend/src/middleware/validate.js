const { z } = require('zod');

// Schemas
const loginSchema = z.object({
  username: z.string().min(3).max(50),
  password: z.string().min(1)
});

const passwordResetRequestSchema = z.object({
  username: z.string().min(3)
});

const passwordResetSchema = z.object({
  username: z.string().min(3),
  codigo: z.string().length(6),
  nuevaPassword: z.string().min(8)
});

const storeRegistrationSchema = z.object({
  nombrePublico: z.string().min(2).max(100),
  tipoEntidad: z.enum(['MIPYME', 'TCP', 'PDL']),
  representanteNombre: z.string().min(2),
  representanteCI: z.string().min(6),
  representanteTelefono: z.string().min(8),
  licenciaEstatal: z.string().min(3),
  direccionCompleta: z.string().min(5),
  zona: z.string().min(1),
  latitud: z.number().min(-90).max(90),
  longitud: z.number().min(-180).max(180),
  categoriaPrincipal: z.string().min(1),
  telefonoWhatsApp: z.string().min(8),
  telefonoRecuperacion: z.string().min(8),
  horario: z.string().min(3),
  entregaInfo: z.string().min(3),
  webUrl: z.string().url().optional().or(z.literal('')),
  username: z.string().min(3).max(50),
  password: z.string().min(8)
});

const productSchema = z.object({
  nombre: z.string().min(1).max(200),
  descripcion: z.string().optional(),
  categoria: z.string().min(1),
  tipoItem: z.enum(['producto', 'servicio']),
  precioCUP: z.number().optional().nullable(),
  precioUSD: z.number().optional().nullable(),
  precioMLC: z.number().optional().nullable(),
  monedaMostrar: z.enum(['CUP', 'USD', 'MLC']).default('CUP'),
  estadoStock: z.enum(['disponible', 'agotado', 'por_encargo', 'no_disponible']).default('disponible'),
  ofertaFlash: z.boolean().default(false)
});

function validate(schema) {
  return (req, res, next) => {
    try {
      req.body = schema.parse(req.body);
      next();
    } catch (err) {
      if (err instanceof z.ZodError) {
        return res.status(400).json({
          error: 'Validación fallida',
          details: err.errors.map(e => ({
            field: e.path.join('.'),
            message: e.message
          }))
        });
      }
      return res.status(400).json({ error: 'Datos inválidos' });
    }
  };
}

module.exports = {
  validate,
  loginSchema,
  passwordResetRequestSchema,
  passwordResetSchema,
  storeRegistrationSchema,
  productSchema
};
